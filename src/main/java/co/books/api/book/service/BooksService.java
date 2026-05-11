package co.books.api.book.service;

import co.books.api.book.dto.BooksDetailDto;
import co.books.api.book.dto.BooksItemDto;
import co.books.api.book.dto.BooksListItemDto;
import co.books.api.book.dto.BooksListResponse;
import co.books.api.book.dto.BooksReviewItemDto;
import co.books.api.book.dto.BooksTopNData;
import co.books.api.book.dto.PageInfo;
import co.books.api.book.dto.SelectedBookItemDto;
import co.books.api.book.entity.BookEntity;
import co.books.api.book.repo.BookRepository;
import co.books.api.review.repo.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

/**
 * 도서 조회 서비스.
 * 메인 페이지 Top-N 묶음 조회와 도서 상세 (리뷰 포함) 조회를 제공한다.
 */
@Service
@RequiredArgsConstructor
public class BooksService {

    private static final String FLAG_Y = "Y";
    private static final String FLAG_N = "N";

    /** 메뉴별 리스트의 기본 페이지 크기 (명세 7번) */
    private static final int DEFAULT_PAGE_SIZE = 10;

    /** 카테고리 슬러그 매핑이 아닌, 플래그 컬럼으로 조회하는 특수 메뉴 키 */
    private static final String CATEGORY_NEW = "new";
    private static final String CATEGORY_BESTSELLER = "bestseller";

    /** 명세 5번: 출간일 최신순, 같으면 제목 오름차순 */
    private static final Sort DEFAULT_SORT =
            Sort.by(Sort.Order.desc("publishDate"), Sort.Order.asc("title"));

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 메인 페이지 Top-N 데이터를 조회한다.
     * 정렬: 출간일 최신순, 같으면 제목 오름차순.
     */
    @Transactional(readOnly = true)
    public BooksTopNData getTopN() {
        List<BooksItemDto> bestTopN = bookRepository
                .findTop5ByIsBestsellerOrderByPublishDateDescTitleAsc(FLAG_Y)
                .stream()
                .map(BooksItemDto::from)
                .toList();

        List<BooksItemDto> newTopN = bookRepository
                .findTop5ByIsNewOrderByPublishDateDescTitleAsc(FLAG_Y)
                .stream()
                .map(BooksItemDto::from)
                .toList();

        // 기본서: 베스트셀러도 아니고 신간도 아닌 도서.
        List<BooksItemDto> basicTopN = bookRepository
                .findTop5ByIsBestsellerAndIsNewOrderByPublishDateDescTitleAsc(FLAG_N, FLAG_N)
                .stream()
                .map(BooksItemDto::from)
                .toList();

        return new BooksTopNData(bestTopN, newTopN, basicTopN);
    }

    /**
     * 메뉴별 도서 리스트를 페이징 조회한다.
     *
     * <ul>
     *   <li>category = "new" → books.is_new = 'Y' 필터</li>
     *   <li>category = "bestseller" → books.is_bestseller = 'Y' 필터</li>
     *   <li>그 외 (web/mobile/basic 등) → categories.slug 매핑</li>
     * </ul>
     *
     * 검색어(search) 가 주어지면 제목 또는 저자에 포함되는 도서만 반환한다.
     * 응답에 담길 nowPageNum 은 1 부터 시작하는 페이지 번호이므로,
     * 입력이 1 미만이면 1 로 보정해 사용한다.
     */
    @Transactional(readOnly = true)
    public BooksListResponse getList(String category, String search, Integer page, Integer size) {
        int safePage = (page == null || page < 1) ? 1 : page;
        int safeSize = (size == null || size < 1) ? DEFAULT_PAGE_SIZE : size;
        // Spring Data 의 Pageable 은 0-based 이므로 1 을 빼서 변환한다.
        Pageable pageable = PageRequest.of(safePage - 1, safeSize, DEFAULT_SORT);

        // 검색어가 없으면 빈 문자열로 두어 LIKE '%%' 가 모든 행을 매칭하도록 한다.
        // (PostgreSQL JDBC 가 null 파라미터를 bytea 로 추론해 LIKE 비교가 실패하는 문제를 회피한다.)
        String keyword = (search == null) ? "" : search.trim();

        Page<BookEntity> result = switch (category == null ? "" : category) {
            case CATEGORY_NEW -> bookRepository.findByIsNewFlag(FLAG_Y, keyword, pageable);
            case CATEGORY_BESTSELLER -> bookRepository.findByIsBestsellerFlag(FLAG_Y, keyword, pageable);
            default -> bookRepository.findByCategorySlug(category, keyword, pageable);
        };

        List<BooksListItemDto> data = result.getContent().stream()
                .map(BooksListItemDto::from)
                .toList();

        PageInfo pageInfo = new PageInfo(safePage, result.getTotalElements());
        return BooksListResponse.ok(data, pageInfo);
    }

    /**
     * 선택된 도서 정보를 조회한다.
     * orderBooks 는 콤마(,) 로 구분된 bookId 문자열이며,
     * 해당 bookId 들에 매칭되는 도서 정보를 리스트로 반환한다.
     */
    @Transactional(readOnly = true)
    public List<SelectedBookItemDto> getSelected(String orderBooks) {
        if (orderBooks == null || orderBooks.isBlank()) {
            return List.of();
        }

        List<String> bookIds = Arrays.stream(orderBooks.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (bookIds.isEmpty()) {
            return List.of();
        }

        return bookRepository.findAllById(bookIds).stream()
                .map(SelectedBookItemDto::from)
                .toList();
    }

    /**
     * 도서 한 권의 상세 정보 + 리뷰 목록을 조회한다.
     * 존재하지 않는 도서는 404 로 응답한다.
     */
    @Transactional(readOnly = true)
    public BooksDetailDto getDetail(String bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "도서를 찾을 수 없습니다: " + bookId));

        List<BooksReviewItemDto> reviewList = reviewRepository
                .findByBookIdOrderByCreatedAtDesc(bookId)
                .stream()
                .map(BooksReviewItemDto::from)
                .toList();

        return BooksDetailDto.of(book, reviewList);
    }
}
