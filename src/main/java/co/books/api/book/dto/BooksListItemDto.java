package co.books.api.book.dto;

import co.books.api.book.entity.BookEntity;

import java.time.LocalDate;

/**
 * 메뉴별 도서 리스트 응답에 사용되는 단일 도서 항목 DTO.
 * 명세상 노출 필드: id, title, subtitle, 저자, 가격(판매가), 출판일.
 * imageUrl 은 목록 화면 썸네일용으로 추가된 필드.
 */
public record BooksListItemDto(
        String bookId,
        String title,
        String subtitle,
        String author,
        Integer price,
        LocalDate publishDate,
        String imageUrl
) {
    public static BooksListItemDto from(BookEntity book) {
        return new BooksListItemDto(
                book.getBookId(),
                book.getTitle(),
                book.getSubtitle(),
                book.getAuthor(),
                book.getSalePrice(),
                book.getPublishDate(),
                book.getImageUrl()
        );
    }
}
