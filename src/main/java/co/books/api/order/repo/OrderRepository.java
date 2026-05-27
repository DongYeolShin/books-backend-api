package co.books.api.order.repo;

import co.books.api.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 주문 리포지토리.
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    /** userId 기준으로 orderedAt 역순 최근 5건 조회 */
    List<OrderEntity> findTop5ByUserIdOrderByOrderedAtDesc(String userId);
}
