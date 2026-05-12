package co.books.api.cart.dto;

/**
 * 장바구니 도서 삭제 API 응답 본문.
 * 성공/실패 모두 {@code { code, message }} 구조로 반환한다.
 */
public record CartDeleteResponse(
        int code,
        String message
) {
    public static CartDeleteResponse ok() {
        return new CartDeleteResponse(200, "삭제되었습니다.");
    }

    public static CartDeleteResponse fail() {
        return new CartDeleteResponse(500, "삭제 실패되었습니다.");
    }
}
