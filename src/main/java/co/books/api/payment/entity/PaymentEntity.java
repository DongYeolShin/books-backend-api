package co.books.api.payment.entity;

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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

/**
 * 결제 엔티티. payments 테이블과 매핑된다.
 *
 * <p>raw_response 는 PostgreSQL JSONB 컬럼이며 직렬화된 포트원 응답 원본을 저장한다.</p>
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
public class PaymentEntity {

    /** 결제 ID = 포트원 paymentId. orderId 와 동일하게 사용한다. */
    @Id
    @Column(name = "payment_id", length = 100)
    private String paymentId;

    @Column(name = "order_id", nullable = false, length = 100)
    private String orderId;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    /** 포트원 거래 ID (transactionId) */
    @Column(name = "tx_id", length = 100)
    private String txId;

    /** 포트원 채널 키 */
    @Column(name = "channel_key", nullable = false, length = 100)
    private String channelKey;

    /** PG 사 코드 (TOSSPAYMENTS, KCP, NICE 등) */
    @Column(name = "pg_provider", length = 50)
    private String pgProvider;

    /** 결제 상태 (payment_status PG ENUM). PgUnknownStringJdbcType 으로 type-unknown 바인딩한다. */
    @Convert(converter = PaymentStatusConverter.class)
    @JdbcType(PgUnknownStringJdbcType.class)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    /** 결제 수단 (pay_method PG ENUM). PgUnknownStringJdbcType 으로 type-unknown 바인딩한다. */
    @Convert(converter = PayMethodConverter.class)
    @JdbcType(PgUnknownStringJdbcType.class)
    @Column(name = "pay_method")
    private PayMethod payMethod;

    /** 포트원 응답으로 검증된 결제 금액 */
    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    /** 통화 (기본 KRW) */
    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "KRW";

    /** 주문명 */
    @Column(name = "order_name", nullable = false, length = 200)
    private String orderName;

    /** 결제 실패 사유 */
    @Column(name = "fail_reason", columnDefinition = "text")
    private String failReason;

    /** 결제 실패 코드 */
    @Column(name = "fail_code", length = 50)
    private String failCode;

    /** 포트원 응답 원본 (JSONB) */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_response", columnDefinition = "jsonb")
    private String rawResponse;

    /** 결제 승인 일시 */
    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
