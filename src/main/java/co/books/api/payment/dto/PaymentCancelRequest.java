package co.books.api.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 결제 취소 요청 본문.
 * cancelAmount 가 null 이면 전액 취소, 값이 있으면 부분 취소로 처리한다.
 */
public record PaymentCancelRequest(
        /** 부분 취소 금액. null 이면 전액 취소 */
        @Min(value = 1, message = "취소 금액은 1원 이상이어야 합니다.")
        Integer cancelAmount,

        /** 취소 사유 (포트원 API 에 전달, 필수) */
        @NotBlank(message = "취소 사유는 필수입니다.")
        String reason
) {
}
