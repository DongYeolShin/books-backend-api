package co.books.api.payment.dto;

/**
 * 결제 취소 응답 본문.
 */
public record PaymentCancelResponse(
        String paymentId,
        /** 취소 후 결제 상태 (전액취소: CANCELLED, 부분취소: PAID 유지) */
        String status,
        /** 이번 취소에서 취소된 금액 */
        Integer cancelledAmount,
        /** 포트원 응답 기준 누적 취소 금액 */
        Integer totalCancelledAmount,
        String message
) {
}
