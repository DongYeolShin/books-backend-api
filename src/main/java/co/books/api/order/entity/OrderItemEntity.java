package co.books.api.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 주문 상세 (한 주문에 담긴 도서 한 권) 엔티티.
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    /** 주문 ID */
    @Column(name = "order_id", nullable = false, length = 100)
    private String orderId;

    /** 도서 ID */
    @Column(name = "book_id", nullable = false, length = 100)
    private String bookId;

    /** 주문 수량 */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /** 구매 시점 가격 (현재가 변동과 무관하게 보존) */
    @Column(name = "price_at_purchase", nullable = false)
    private Integer priceAtPurchase;
}
