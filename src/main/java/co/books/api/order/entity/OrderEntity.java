package co.books.api.order.entity;

import co.books.api.common.jpa.PgUnknownStringJdbcType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * 주문 마스터 엔티티. orders 테이블과 매핑된다.
 *
 * <p>order_status 는 PostgreSQL ENUM 이며 {@link OrderStatusConverter} 로 변환한다.</p>
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderEntity {

    /** 주문 ID (ord_yyyyMMdd_xxxxxxxx 형태로 서비스에서 생성) */
    @Id
    @Column(name = "order_id", length = 100)
    private String orderId;

    /** 주문자 회원 ID */
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    /** 수령인 이름 */
    @Column(name = "receiver", nullable = false, length = 100)
    private String receiver;

    /** 수령인 연락처 */
    @Column(name = "phone", nullable = false, length = 100)
    private String phone;

    /** 결제(요청) 총 금액. 서버 재계산 결과를 저장한다. */
    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    /**
     * 주문 상태 (order_status PG ENUM).
     * {@link OrderStatusConverter} 가 enum ↔ 소문자 문자열 변환을 수행하고,
     * {@link PgUnknownStringJdbcType} 이 JDBC 바인딩을 setObject(Types.OTHER) 로 처리해
     * PostgreSQL 이 자동으로 order_status 타입으로 캐스트하도록 한다.
     */
    @Convert(converter = OrderStatusConverter.class)
    @JdbcType(PgUnknownStringJdbcType.class)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    /** 배송지 기본 주소 */
    @Column(name = "shipping_address", nullable = false, length = 255)
    private String shippingAddress;

    /** 배송지 상세 주소 */
    @Column(name = "shipping_detail_address", length = 255)
    private String shippingDetailAddress = "";

    @CreationTimestamp
    @Column(name = "ordered_at", nullable = false, updatable = false)
    private OffsetDateTime orderedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * 결제 결과에 따라 주문 상태를 갱신한다.
     */
    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
    }
}
