package co.books.api.book.repo;

import co.books.api.book.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 도서 리포지토리.
 * books 테이블에 대한 기본 CRUD 와 자주 쓰이는 조회 메서드를 제공한다.
 */
@Repository
public interface BookRepository extends JpaRepository<BookEntity, String> {

    /** ISBN 으로 도서 단건 조회 */
    Optional<BookEntity> findByIsbn(String isbn);

    /** 베스트셀러 여부로 도서 목록 조회 ('Y' / 'N') */
    List<BookEntity> findByIsBestseller(String isBestseller);

    /** 신간 여부로 도서 목록 조회 ('Y' / 'N') */
    List<BookEntity> findByIsNew(String isNew);

    /** 제목에 키워드가 포함된 도서 검색 */
    List<BookEntity> findByTitleContaining(String keyword);

    /**
     * 베스트셀러 Top 5.
     * 출간일 최신순, 같으면 제목 오름차순.
     */
    List<BookEntity> findTop5ByIsBestsellerOrderByPublishDateDescTitleAsc(String isBestseller);

    /**
     * 신간 Top 5.
     * 출간일 최신순, 같으면 제목 오름차순.
     */
    List<BookEntity> findTop5ByIsNewOrderByPublishDateDescTitleAsc(String isNew);

    /**
     * 기본서 Top 5 (베스트셀러도 아니고 신간도 아닌 도서).
     * 출간일 최신순, 같으면 제목 오름차순.
     */
    List<BookEntity> findTop5ByIsBestsellerAndIsNewOrderByPublishDateDescTitleAsc(
            String isBestseller, String isNew);

    /**
     * 카테고리 슬러그 + 검색어로 도서 목록을 페이징 조회한다.
     * (book_categories 조인 → categories.slug 비교) 후 제목 또는 저자에 keyword 가 포함된 행만 반환한다.
     * keyword 는 빈 문자열을 넘기면 LIKE '%%' 가 되어 모든 행이 매칭된다.
     * (title/author 컬럼은 NOT NULL 이므로 NULL 비교 문제는 발생하지 않는다.)
     * 정렬은 Pageable 의 Sort 로 지정한다.
     */
    @Query("""
            SELECT b FROM BookEntity b
            JOIN b.bookCategories bc
            JOIN bc.category c
            WHERE c.slug = :slug
              AND ( b.title LIKE CONCAT('%', :keyword, '%')
                    OR b.author LIKE CONCAT('%', :keyword, '%') )
            """)
    Page<BookEntity> findByCategorySlug(@Param("slug") String slug,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);

    /**
     * 신간 플래그 + 검색어로 도서 목록을 페이징 조회한다.
     * keyword 는 빈 문자열을 넘기면 모든 행이 매칭된다.
     */
    @Query("""
            SELECT b FROM BookEntity b
            WHERE b.isNew = :flag
              AND ( b.title LIKE CONCAT('%', :keyword, '%')
                    OR b.author LIKE CONCAT('%', :keyword, '%') )
            """)
    Page<BookEntity> findByIsNewFlag(@Param("flag") String flag,
                                     @Param("keyword") String keyword,
                                     Pageable pageable);

    /**
     * 베스트셀러 플래그 + 검색어로 도서 목록을 페이징 조회한다.
     * keyword 는 빈 문자열을 넘기면 모든 행이 매칭된다.
     */
    @Query("""
            SELECT b FROM BookEntity b
            WHERE b.isBestseller = :flag
              AND ( b.title LIKE CONCAT('%', :keyword, '%')
                    OR b.author LIKE CONCAT('%', :keyword, '%') )
            """)
    Page<BookEntity> findByIsBestsellerFlag(@Param("flag") String flag,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);
}
