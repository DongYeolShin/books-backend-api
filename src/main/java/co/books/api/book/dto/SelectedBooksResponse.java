package co.books.api.book.dto;

import java.util.List;

/**
 * 선택된 책 정보 API 응답 래퍼.
 * { code, data } 구조를 따른다.
 */
public record SelectedBooksResponse(
        int code,
        List<SelectedBookItemDto> data
) {
    public static SelectedBooksResponse ok(List<SelectedBookItemDto> data) {
        return new SelectedBooksResponse(200, data);
    }
}
