package co.books.api.payment.entity;

/**
 * 결제 수단. PostgreSQL pay_method ENUM 과 매핑된다.
 * DB 값은 모두 소문자.
 */
public enum PayMethod {
    CARD,
    VIRTUAL_ACCOUNT,
    TRANSFER,
    MOBILE,
    EASY_PAY;

    public String toDbValue() {
        return switch (this) {
            case CARD -> "card";
            case VIRTUAL_ACCOUNT -> "virtual_account";
            case TRANSFER -> "transfer";
            case MOBILE -> "mobile";
            case EASY_PAY -> "easy_pay";
        };
    }

    public static PayMethod fromDbValue(String v) {
        if (v == null) return null;
        return switch (v.toLowerCase()) {
            case "card" -> CARD;
            case "virtual_account" -> VIRTUAL_ACCOUNT;
            case "transfer" -> TRANSFER;
            case "mobile" -> MOBILE;
            case "easy_pay" -> EASY_PAY;
            default -> throw new IllegalArgumentException("알 수 없는 pay_method: " + v);
        };
    }

    /**
     * 포트원 V2 응답의 method.type 문자열을 결제 수단으로 변환한다.
     * 매칭되지 않으면 null 을 반환한다.
     */
    public static PayMethod fromPortOneMethodType(String type) {
        if (type == null) return null;
        return switch (type) {
            case "PaymentMethodCard" -> CARD;
            case "PaymentMethodVirtualAccount" -> VIRTUAL_ACCOUNT;
            case "PaymentMethodTransfer" -> TRANSFER;
            case "PaymentMethodMobile" -> MOBILE;
            case "PaymentMethodEasyPay" -> EASY_PAY;
            default -> null;
        };
    }
}
