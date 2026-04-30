package co.books.api.book.dto;

import co.books.api.book.entity.BookEntity;

/**
 * 메인 페이지 도서 목록 응답에 사용되는 단일 도서 항목 DTO.
 * 판매가(sale_price)를 price 로 노출한다.
 */
public record BooksItemDto(
        String bookId,
        String title,
        String author,
        Integer price,
        String imageUrl
) {
    public static BooksItemDto from(BookEntity book) {
        return new BooksItemDto(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getSalePrice(),
                book.getImageUrl()
        );
    }
}
