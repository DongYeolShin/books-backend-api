package co.books.api.order.entity;

/**
 * 주문 상태. PostgreSQL order_status ENUM 과 매핑된다.
 *
 * <p>DB ENUM 값은 소문자(pending/paid/...)이므로
 * {@link OrderStatusConverter} 에서 toLowerCase / toUpperCase 변환을 처리한다.</p>
 */
public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    FAILED;

    /**
     * 결제 실패는 schema 의 order_status ENUM 에 없으므로 cancelled 로 매핑한다.
     * 단, 본 프로젝트는 prompt 코드의 OrderStatus.FAILED 분기를 유지하기 위해
     * Java enum 에는 FAILED 를 두되 DB 저장 시 'cancelled' 로 매핑한다.
     */
    public String toDbValue() {
        return switch (this) {
            case PENDING -> "pending";
            case PAID -> "paid";
            case SHIPPED -> "shipped";
            case DELIVERED -> "delivered";
            case CANCELLED, FAILED -> "cancelled";
        };
    }

    public static OrderStatus fromDbValue(String v) {
        if (v == null) return null;
        return switch (v.toLowerCase()) {
            case "pending" -> PENDING;
            case "paid" -> PAID;
            case "shipped" -> SHIPPED;
            case "delivered" -> DELIVERED;
            case "cancelled" -> CANCELLED;
            default -> throw new IllegalArgumentException("알 수 없는 order_status: " + v);
        };
    }
}
