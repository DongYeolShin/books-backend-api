package co.books.api.cart.dto;

import java.util.List;

/**
 * 장바구니 리스트 API 응답 래퍼.
 * { code, data } 구조를 따른다.
 */
public record CartListResponse(
        int code,
        List<CartListItemDto> data
) {
    public static CartListResponse ok(List<CartListItemDto> data) {
        return new CartListResponse(200, data);
    }
}
