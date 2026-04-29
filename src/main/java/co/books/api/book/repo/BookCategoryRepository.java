package co.books.api.book.repo;

import co.books.api.book.entity.BookCategoryEntity;
import co.books.api.book.entity.BookCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 도서-카테고리 매핑 리포지토리.
 * book_categories 테이블에 대한 기본 CRUD 및 단방향 조회를 제공한다.
 */
@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategoryEntity, BookCategoryId> {

    /** 특정 도서에 연결된 매핑 목록 */
    List<BookCategoryEntity> findByIdBookId(String bookId);

    /** 특정 카테고리에 연결된 매핑 목록 */
    List<BookCategoryEntity> findByIdCategoryId(Integer categoryId);

    /** 특정 도서의 모든 카테고리 매핑 삭제 */
    void deleteByIdBookId(String bookId);
}
