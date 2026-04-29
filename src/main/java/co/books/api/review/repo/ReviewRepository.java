package co.books.api.review.repo;

import co.books.api.review.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 도서 리뷰 리포지토리.
 * reviews 테이블에 대한 기본 CRUD 와 도서별 / 회원별 조회 메서드를 제공한다.
 */
@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    /** 특정 도서의 리뷰 목록 (최신순) */
    List<ReviewEntity> findByBookIdOrderByCreatedAtDesc(String bookId);

    /** 특정 회원이 작성한 리뷰 목록 (최신순) */
    List<ReviewEntity> findByUserIdOrderByCreatedAtDesc(String userId);
}
