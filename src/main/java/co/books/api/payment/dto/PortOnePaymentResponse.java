package co.books.api.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * 포트원 V2 결제 단건 조회 응답 DTO (필요한 필드만 매핑).
 *
 * <p>전체 응답은 PaymentEntity.rawResponse(JSONB) 에 원본 그대로 보관한다.</p>
 *
 * @see <a href="https://developers.portone.io/api/rest-v2/payment">포트원 V2 결제 API</a>
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortOnePaymentResponse {

    /** 결제 상태 (PAID / FAILED / VIRTUAL_ACCOUNT_ISSUED / ...) */
    private String status;

    /** 포트원 거래 ID */
    private String transactionId;

    /** 결제 채널 정보 */
    private Channel channel;

    /** 결제 수단 정보 */
    private Method method;

    /** 금액 정보 */
    private Amount amount;

    /** 주문명 */
    private String orderName;

    /** 결제 승인 일시 */
    private OffsetDateTime paidAt;

    /** 실패 정보 (status=FAILED 인 경우에만 채워짐) */
    private Failure failure;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Channel {
        private String key;
        private String pgProvider;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Method {
        /** "PaymentMethodCard" / "PaymentMethodEasyPay" 등 */
        private String type;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount {
        private Integer total;
        private Integer taxFree;
        private Integer vat;
        private String currency;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Failure {
        private String reason;
        private String pgCode;
    }
}
