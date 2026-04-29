package co.books.api.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * 도서 리뷰 엔티티.
 * reviews 테이블과 매핑된다. (user_id, book_id) 조합은 유니크 제약이 걸려 있어
 * 회원당 도서 1 권에 리뷰를 1 개만 작성할 수 있다.
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
public class ReviewEntity {

    /** 리뷰 ID (DB 자동 채번) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    /** 작성자 회원 ID */
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    /** 대상 도서 ID */
    @Column(name = "book_id", nullable = false, length = 100)
    private String bookId;

    /** 별점 (1~5) */
    @Column(nullable = false)
    private Short rating;

    /** 리뷰 내용 */
    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
