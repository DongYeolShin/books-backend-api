package co.books.api.order.dto;

/**
 * 구매목록 단일 주문 항목 DTO.
 *
 * <p>주문 마스터(orders) 위주의 정보로 구성하며, 화면 노출용 대표 이미지는
 * 해당 주문의 첫 번째 OrderItem 의 도서 이미지(URL) 를 사용한다.</p>
 */
public record OrderListItemDto(
        String orderId,
        String orderDate,
        String orderName,
        Integer totalAmount,
        String status,
        String imageUrl
) {
}
