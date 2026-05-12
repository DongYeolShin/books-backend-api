package co.books.api.order.dto;

/**
 * 주문 생성 응답 본문.
 * 결제 단계에서 사용할 orderId / 서버 재계산 금액 / 주문명을 반환한다.
 */
public record CreateOrderResponse(
        String orderId,
        Integer totalAmount,
        String orderName
) {
}
