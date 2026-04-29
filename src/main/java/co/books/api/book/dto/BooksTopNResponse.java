package co.books.api.book.dto;

/**
 * 메인 페이지 Top-N API 의 응답 래퍼.
 * 명세에 맞춰 code 와 data 만을 가진다.
 */
public record BooksTopNResponse(
        int code,
        BooksTopNData data
) {
    public static BooksTopNResponse ok(BooksTopNData data) {
        return new BooksTopNResponse(200, data);
    }
}
