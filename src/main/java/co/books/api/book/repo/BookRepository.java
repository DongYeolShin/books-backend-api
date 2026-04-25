package co.books.api.book.repo;

import co.books.api.book.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 도서 리포지토리.
 * books 테이블에 대한 기본 CRUD 와 자주 쓰이는 조회 메서드를 제공한다.
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    /** ISBN 으로 도서 단건 조회 */
    Optional<BookEntity> findByIsbn(String isbn);

    /** 베스트셀러 여부로 도서 목록 조회 ('Y' / 'N') */
    List<BookEntity> findByIsBestseller(String isBestseller);

    /** 신간 여부로 도서 목록 조회 ('Y' / 'N') */
    List<BookEntity> findByIsNew(String isNew);

    /** 제목에 키워드가 포함된 도서 검색 */
    List<BookEntity> findByTitleContaining(String keyword);
}
