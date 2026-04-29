package co.books.api.book.dto;

import co.books.api.review.entity.ReviewEntity;

import java.time.OffsetDateTime;

/**
 * 도서 상세 응답에 포함되는 단일 리뷰 항목 DTO.
 */
public record BooksReviewItemDto(
        Long reviewId,
        String userId,
        Short rating,
        String content,
        OffsetDateTime createdAt
) {
    public static BooksReviewItemDto from(ReviewEntity review) {
        return new BooksReviewItemDto(
                review.getReviewId(),
                review.getUserId(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt()
        );
    }
}
