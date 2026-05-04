package co.books.api.book.dto;

import java.util.List;

/**
 * 메뉴별 도서 리스트 API 응답 래퍼.
 * { code, data, pageInfo } 구조를 따른다.
 */
public record BooksListResponse(
        int code,
        List<BooksListItemDto> data,
        PageInfo pageInfo
) {
    public static BooksListResponse ok(List<BooksListItemDto> data, PageInfo pageInfo) {
        return new BooksListResponse(200, data, pageInfo);
    }
}
