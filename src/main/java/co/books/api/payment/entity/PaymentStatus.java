package co.books.api.payment.entity;

/**
 * 결제 상태. PostgreSQL payment_status ENUM 과 매핑된다.
 * DB 값은 모두 소문자.
 */
public enum PaymentStatus {
    PAID,
    FAILED,
    VIRTUAL_ACCOUNT_ISSUED;

    public String toDbValue() {
        return switch (this) {
            case PAID -> "paid";
            case FAILED -> "failed";
            case VIRTUAL_ACCOUNT_ISSUED -> "virtual_account_issued";
        };
    }

    public static PaymentStatus fromDbValue(String v) {
        if (v == null) return null;
        return switch (v.toLowerCase()) {
            case "paid" -> PAID;
            case "failed" -> FAILED;
            case "virtual_account_issued" -> VIRTUAL_ACCOUNT_ISSUED;
            default -> throw new IllegalArgumentException("알 수 없는 payment_status: " + v);
        };
    }

    /**
     * 포트원 V2 응답 status 문자열을 결제 상태로 변환한다.
     * PAID/FAILED/VIRTUAL_ACCOUNT_ISSUED 외의 값은 FAILED 로 간주한다.
     */
    public static PaymentStatus fromPortOne(String s) {
        if (s == null) return FAILED;
        return switch (s.toUpperCase()) {
            case "PAID" -> PAID;
            case "VIRTUAL_ACCOUNT_ISSUED" -> VIRTUAL_ACCOUNT_ISSUED;
            default -> FAILED;
        };
    }
}
