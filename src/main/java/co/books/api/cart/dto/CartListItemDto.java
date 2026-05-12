package co.books.api.cart.dto;

/**
 * 장바구니 리스트의 항목.
 * cart_items 와 books 를 조인하여 도서 정보(title, author, 가격)와 담긴 수량을 함께 반환한다.
 */
public record CartListItemDto(
        String bookId,
        String title,
        String author,
        Integer salePrice,
        Integer originalPrice,
        Integer quantity
) {
}
