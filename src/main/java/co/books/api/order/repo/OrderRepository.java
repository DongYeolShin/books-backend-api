package co.books.api.order.repo;

import co.books.api.order.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 주문 리포지토리.
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    /** userId 기준으로 orderedAt 역순 최근 5건 조회 */
    List<OrderEntity> findTop5ByUserIdOrderByOrderedAtDesc(String userId);

    /**
     * 마이페이지용: userId 의 결제 완료(PAID) 상태 주문 최근 3건을 orderedAt 역순으로 조회.
     *
     * <p>order_status 는 PostgreSQL ENUM 이라 JPQL enum 리터럴이 작동하지 않으므로 native query 로 작성한다.
     * JDBC URL 의 {@code stringtype=unspecified} 옵션 덕에 문자열이 자동 캐스팅된다.</p>
     */
    @Query(
            value = "SELECT * FROM orders WHERE user_id = :userId "
                  + "AND status = 'paid' "
                  + "ORDER BY ordered_at DESC LIMIT 3",
            nativeQuery = true
    )
    List<OrderEntity> findTop3CompletedByUserId(@Param("userId") String userId);

    /**
     * PENDING 상태이면서 orderedAt 이 threshold 이전인 만료 주문 조회 (만료 스케줄러용).
     *
     * <p>order_status 는 PostgreSQL ENUM 이므로 JPQL 의 enum 리터럴 비교는 동작하지 않는다
     * (Hibernate 가 enum 을 단순 문자열로 바인딩하면서 PG 가 캐스팅 실패).
     * 따라서 native query 로 작성하며, JDBC URL 의 {@code stringtype=unspecified} 옵션 덕에
     * 'pending' 문자열이 자동으로 order_status 로 캐스트된다.</p>
     */
    @Query(
            value = "SELECT * FROM orders WHERE status = 'pending' AND ordered_at < :threshold",
            nativeQuery = true
    )
    List<OrderEntity> findExpiredPendingOrders(@Param("threshold") OffsetDateTime threshold);

    /**
     * 구매목록(마이페이지 주문내역) 페이징 조회.
     * 결제완료(paid) / 배송중(shipped) / 배송완료(delivered) 상태만 포함하며,
     * 주문 취소(cancelled) 는 제외한다. orderedAt 역순 정렬.
     *
     * <p>order_status 가 PostgreSQL ENUM 이라 JPQL enum 리터럴이 동작하지 않으므로
     * native query 로 작성한다. Pageable 의 정렬 정보는 사용하지 않고 ORDER BY 를 명시한다.</p>
     */
    @Query(
            value = "SELECT * FROM orders WHERE user_id = :userId "
                  + "AND status IN ('paid', 'shipped', 'delivered') "
                  + "ORDER BY ordered_at DESC",
            countQuery = "SELECT count(*) FROM orders WHERE user_id = :userId "
                       + "AND status IN ('paid', 'shipped', 'delivered')",
            nativeQuery = true
    )
    Page<OrderEntity> findCompletedByUserId(@Param("userId") String userId, Pageable pageable);
}
