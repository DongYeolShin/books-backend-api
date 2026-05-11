package co.books.api.book.dto;

import co.books.api.book.entity.BookEntity;

/**
 * 선택된 책 정보 응답에 사용되는 단일 도서 항목 DTO.
 * 명세상 노출 필드: 책제목, 원가격, 할인가격, 이미지. (bookId 는 식별용으로 함께 포함)
 */
public record SelectedBookItemDto(
        String bookId,
        String title,
        Integer originalPrice,
        Integer salePrice,
        String imageUrl
) {
    public static SelectedBookItemDto from(BookEntity book) {
        return new SelectedBookItemDto(
                book.getBookId(),
                book.getTitle(),
                book.getOriginalPrice(),
                book.getSalePrice(),
                book.getImageUrl()
        );
    }
}
