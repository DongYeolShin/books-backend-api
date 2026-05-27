package co.books.api.payment.dto;

/**
 * 주문 상품 항목.
 * originalPrice: books.original_price (현재 도서 정가).
 * salePrice: order_items.price_at_purchase (구매 시점 가격).
 */
public record OrderCompleteItem(
        String bookId,
        String title,
        int originalPrice,
        int salePrice,
        int quantity
) {}
