package co.books.api.user.dto;

/** 마이페이지 최근 주문 항목 DTO. */
public record RecentOrderDto(
        String orderId,
        String orderDate,
        String title,
        String author,
        Integer amount,
        String status
) {
}
