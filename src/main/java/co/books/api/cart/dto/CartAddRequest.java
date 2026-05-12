package co.books.api.cart.dto;

/**
 * 장바구니 등록 요청 본문.
 * {@code { "bookId": "BOOK001", "quantity": 1 }} 형태의 JSON 과 매핑된다.
 */
public record CartAddRequest(
        String bookId,
        Integer quantity
) {
}
