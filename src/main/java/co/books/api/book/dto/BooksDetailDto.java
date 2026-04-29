package co.books.api.book.dto;

import co.books.api.book.entity.BookEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * 도서 상세 페이지 응답의 data 필드.
 * 도서 기본 정보 + 리뷰 목록을 함께 담는다.
 */
public record BooksDetailDto(
        String bookId,
        String title,
        String subtitle,
        String author,
        String publisher,
        LocalDate publishDate,
        Integer originalPrice,
        Integer salePrice,
        String description,
        String imageUrl,
        Integer stock,
        List<BooksReviewItemDto> reviewList
) {
    public static BooksDetailDto of(BookEntity book, List<BooksReviewItemDto> reviewList) {
        return new BooksDetailDto(
                book.getBookId(),
                book.getTitle(),
                book.getSubtitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishDate(),
                book.getOriginalPrice(),
                book.getSalePrice(),
                book.getDescription(),
                book.getImageUrl(),
                book.getStock(),
                reviewList
        );
    }
}
