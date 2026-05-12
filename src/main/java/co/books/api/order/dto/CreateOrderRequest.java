package co.books.api.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

/**
 * 주문 생성 요청 본문.
 */
public record CreateOrderRequest(
        @NotBlank(message = "receiver 는 필수입니다.")
        String receiver,
        @NotBlank(message = "phone 은 필수입니다.")
        String phone,
        @NotBlank(message = "shippingAddress 는 필수입니다.")
        String shippingAddress,
        String shippingDetailAddress,
        @PositiveOrZero(message = "usedPoints 는 0 이상이어야 합니다.")
        Integer usedPoints,
        @NotEmpty(message = "items 는 최소 1건 이상이어야 합니다.")
        @Valid
        List<OrderItemRequest> items
) {
}
