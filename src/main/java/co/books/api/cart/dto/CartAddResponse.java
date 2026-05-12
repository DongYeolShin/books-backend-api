package co.books.api.cart.dto;

/**
 * 장바구니 등록 API 응답 본문.
 * 성공/실패 모두 {@code { code, message }} 구조로 반환한다.
 */
public record CartAddResponse(
        int code,
        String message
) {
    public static CartAddResponse ok() {
        return new CartAddResponse(200, "장바구니 등록이 되었습니다");
    }

    public static CartAddResponse fail() {
        return new CartAddResponse(500, "장바구니 등록이 실패되었습니다.");
    }
}
