package co.books.api.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 포트원 V2 결제 취소 API 요청 본문.
 * POST /payments/{paymentId}/cancel
 * amount 가 null 이면 전액 취소, 값이 있으면 부분 취소.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PortOneCancelRequest(
        /** 취소 사유 (필수) */
        String reason,
        /** 부분 취소 금액. null 이면 전액 취소 */
        Integer amount
) {
}
