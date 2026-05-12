package co.books.api.cart.repo;

import co.books.api.cart.dto.CartListItemDto;
import co.books.api.cart.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 장바구니 리포지토리.
 * cart_items 테이블에 대한 기본 CRUD 와 회원/도서 기준 조회 메서드를 제공한다.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    /** 회원이 이미 같은 도서를 담아둔 항목을 조회한다. */
    Optional<CartItemEntity> findByUserIdAndBookId(String userId, String bookId);

    /**
     * 장바구니 등록을 PostgreSQL UPSERT 로 처리한다.
     *
     * <p>(user_id, book_id) 유니크 제약을 활용하여,
     * 동일 도서가 이미 담겨 있으면 기존 수량에 누적하고, 없으면 새 행을 삽입한다.
     * 단일 SQL 로 원자적으로 처리되므로, 동시 요청 상황에서 발생할 수 있는
     * find → insert 사이의 race condition (중복 키 위반) 을 방지한다.</p>
     */
    @Modifying
    @Query(value = """
            INSERT INTO cart_items (user_id, book_id, quantity, added_at)
            VALUES (:userId, :bookId, :quantity, CURRENT_TIMESTAMP)
            ON CONFLICT (user_id, book_id)
            DO UPDATE SET quantity = cart_items.quantity + EXCLUDED.quantity
            """, nativeQuery = true)
    int upsertCartItem(@Param("userId") String userId,
                       @Param("bookId") String bookId,
                       @Param("quantity") int quantity);

    /**
     * 특정 회원의 특정 도서 수량을 새 값으로 갱신한다.
     * 영향 받은 행 수를 반환하며, 0 이면 장바구니에 해당 도서가 없음을 의미한다.
     */
    @Modifying
    @Query("""
            UPDATE CartItemEntity c
               SET c.quantity = :quantity
             WHERE c.userId = :userId
               AND c.bookId = :bookId
            """)
    int updateQuantity(@Param("userId") String userId,
                       @Param("bookId") String bookId,
                       @Param("quantity") int quantity);

    /**
     * 특정 회원의 특정 도서를 장바구니에서 삭제한다.
     * 영향 받은 행 수를 반환하며, 0 이면 장바구니에 해당 도서가 없음을 의미한다.
     */
    @Modifying
    @Query("""
            DELETE FROM CartItemEntity c
             WHERE c.userId = :userId
               AND c.bookId = :bookId
            """)
    int deleteByUserIdAndBookId(@Param("userId") String userId,
                                @Param("bookId") String bookId);

    /**
     * 특정 회원의 장바구니 목록을 도서 정보와 함께 조회한다.
     * CartItemEntity 가 BookEntity 와 직접적인 연관관계를 갖지 않으므로,
     * book_id 컬럼을 키로 하여 명시적으로 조인한다.
     * 정렬은 담은 시점(added_at) 최신순.
     */
    @Query("""
            SELECT new co.books.api.cart.dto.CartListItemDto(
                       c.bookId, b.title, b.author,
                       b.salePrice, b.originalPrice, c.quantity)
            FROM CartItemEntity c, BookEntity b
            WHERE c.bookId = b.bookId
              AND c.userId = :userId
            ORDER BY c.addedAt DESC
            """)
    List<CartListItemDto> findCartListByUserId(@Param("userId") String userId);
}
