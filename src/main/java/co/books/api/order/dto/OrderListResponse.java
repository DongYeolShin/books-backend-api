package co.books.api.order.dto;

import co.books.api.book.dto.PageInfo;

import java.util.List;

/**
 * 구매목록(주문내역) 리스트 API 응답 래퍼.
 * { code, data, pageInfo } 구조를 따른다.
 */
public record OrderListResponse(
        int code,
        List<OrderListItemDto> data,
        PageInfo pageInfo
) {
    public static OrderListResponse ok(List<OrderListItemDto> data, PageInfo pageInfo) {
        return new OrderListResponse(200, data, pageInfo);
    }
}
