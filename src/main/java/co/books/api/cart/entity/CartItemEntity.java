package co.books.api.cart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * 장바구니 항목 엔티티.
 * cart_items 테이블과 매핑되며, (user_id, book_id) 조합에 유니크 제약이 있어
 * 한 회원이 같은 도서를 중복으로 담지 못한다.
 */
@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
@Getter
@Setter
public class CartItemEntity {

    /** 장바구니 항목 ID (DB 자동 채번) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    /** 회원 ID */
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    /** 도서 ID */
    @Column(name = "book_id", nullable = false, length = 100)
    private String bookId;

    /** 수량 (1 이상) */
    @Column(nullable = false)
    private Integer quantity = 1;

    @CreationTimestamp
    @Column(name = "added_at", nullable = false, updatable = false)
    private OffsetDateTime addedAt;
}