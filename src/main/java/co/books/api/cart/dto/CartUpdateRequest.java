package co.books.api.cart.dto;

/**
 * 장바구니 수량 변경 요청 본문.
 * {@code { "bookId": "BOOK001", "quantity": 1 }} 형태의 JSON 과 매핑된다.
 * quantity 는 누적값이 아니라 변경 후의 새 수량이다.
 */
public record CartUpdateRequest(
        String bookId,
        Integer quantity
) {
}
