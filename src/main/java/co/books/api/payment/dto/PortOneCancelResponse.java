package co.books.api.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 포트원 V2 결제 취소 API 응답 DTO (필요한 필드만 매핑).
 * POST /payments/{paymentId}/cancel 응답 — payment 객체를 직접 반환한다.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortOneCancelResponse {

    /** 결제 상태 (CANCELLED / PAID 등) */
    private String status;

    /** 금액 정보 */
    private Amount amount;

    /** 취소 내역 목록 */
    private List<Cancellation> cancellations;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount {
        /** 결제 원금 */
        private Integer total;
        /** 누적 취소 금액 */
        private Integer cancelled;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cancellation {
        /** 취소 ID */
        private String cancellationId;
        /** 취소 사유 */
        private String reason;
        /** 취소 금액 */
        private Integer amount;
        /** 취소 일시 */
        private OffsetDateTime cancelledAt;
        /** 취소 상태 (SUCCEEDED 등) */
        private String status;
    }
}
