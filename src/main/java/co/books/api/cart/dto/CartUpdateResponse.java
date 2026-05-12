package co.books.api.cart.dto;

/**
 * 장바구니 수량 변경 API 응답 본문.
 * 성공/실패 모두 {@code { code, message }} 구조로 반환한다.
 */
public record CartUpdateResponse(
        int code,
        String message
) {
    public static CartUpdateResponse ok() {
        return new CartUpdateResponse(200, "수정되었습니다.");
    }

    public static CartUpdateResponse fail() {
        return new CartUpdateResponse(500, "수정이 실패되었습니다.");
    }
}
