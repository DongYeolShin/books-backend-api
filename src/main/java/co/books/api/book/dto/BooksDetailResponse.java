package co.books.api.book.dto;

/**
 * 도서 상세 API 의 응답 래퍼.
 * { code, data } 구조를 따른다.
 */
public record BooksDetailResponse(
        int code,
        BooksDetailDto data
) {
    public static BooksDetailResponse ok(BooksDetailDto data) {
        return new BooksDetailResponse(200, data);
    }
}
