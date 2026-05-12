package co.books.api.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 주문 생성 요청에 포함되는 도서 한 권의 주문 정보.
 *
 * <p>{@code priceAtPurchase} 는 클라이언트가 보내지만 서버는 신뢰하지 않고
 * books.sale_price 로 재계산한다 (★위변조 방지).</p>
 */
public record OrderItemRequest(
        @NotBlank(message = "bookId 는 필수입니다.")
        String bookId,
        @NotNull(message = "quantity 는 필수입니다.")
        @Min(value = 1, message = "quantity 는 1 이상이어야 합니다.")
        Integer quantity,
        Integer priceAtPurchase
) {
}
