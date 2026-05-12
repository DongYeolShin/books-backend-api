package co.books.api.payment.dto;

import co.books.api.payment.entity.PaymentEntity;
import co.books.api.payment.entity.PaymentStatus;

/**
 * 결제 완료(검증) 응답 본문.
 */
public record PaymentCompleteResponse(
        String status,
        String paymentId,
        String message
) {
    public static PaymentCompleteResponse from(PaymentEntity payment) {
        PaymentStatus status = payment.getStatus();
        String msg = switch (status) {
            case PAID -> "결제가 완료되었습니다.";
            case FAILED -> "결제가 실패했습니다.";
            case VIRTUAL_ACCOUNT_ISSUED -> "가상계좌가 발급되었습니다. 입금 후 결제가 완료됩니다.";
        };
        return new PaymentCompleteResponse(status.name(), payment.getPaymentId(), msg);
    }
}
