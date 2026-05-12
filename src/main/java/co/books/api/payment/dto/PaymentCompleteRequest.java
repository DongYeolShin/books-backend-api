package co.books.api.payment.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 결제 완료(검증) 요청 본문.
 * 클라이언트가 포트원 결제 결과를 알려오면, 서버가 paymentId 로 포트원 API 를 재조회하여
 * 금액과 상태를 검증한다.
 */
public record PaymentCompleteRequest(
        @NotBlank(message = "paymentId 는 필수입니다.")
        String paymentId,
        @NotBlank(message = "orderId 는 필수입니다.")
        String orderId
) {
}
