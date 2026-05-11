package co.books.api.book.contrl;

import co.books.api.book.dto.BooksDetailResponse;
import co.books.api.book.dto.BooksListResponse;
import co.books.api.book.dto.BooksTopNResponse;
import co.books.api.book.dto.SelectedBooksResponse;
import co.books.api.book.service.BooksService;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 도서 관련 REST API.
 * 기본 경로: /api/v1/books
 */
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BooksController {

    private final BooksService booksService;

    /**
     * 메인 페이지 Top-N 도서 목록.
     * 베스트셀러 / 신간 / 기본서 각 5 권씩을 묶어 반환한다.
     * 인증 없이 접근 가능하다.
     */
    @GetMapping("/topn")
    public ResponseEntity<BooksTopNResponse> getTopN() {
        return ResponseEntity.ok(BooksTopNResponse.ok(booksService.getTopN()));
    }

    /**
     * 메뉴별 도서 리스트.
     *
     * <ul>
     *   <li>category: 메뉴 식별자 (web/mobile/basic 은 카테고리 slug, new/bestseller 는 플래그 컬럼)</li>
     *   <li>search: 제목/저자 부분 일치 검색어 (선택)</li>
     *   <li>page: 1-based 페이지 번호 (기본 1)</li>
     *   <li>size: 페이지당 행 수 (기본 10)</li>
     * </ul>
     *
     * 정렬은 출간일 최신순, 같으면 제목 오름차순. 인증 없이 접근 가능하다.
     */
    @GetMapping
    public ResponseEntity<BooksListResponse> getList(
            @RequestParam("category") String category,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        return ResponseEntity.ok(booksService.getList(category, keyword, page, size));
    }

    /**
     * 도서 상세 페이지 정보 (리뷰 목록 포함).
     * 인증 없이 접근 가능하다.
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BooksDetailResponse> getDetail(@PathVariable String bookId) {
        return ResponseEntity.ok(BooksDetailResponse.ok(booksService.getDetail(bookId)));
    }

    /**
     * 선택된 책 정보 조회.
     * order-books 파라미터는 콤마(,) 로 구분된 bookId 문자열이다.
     * 예: /api/v1/books?order-books=BOOK001,BOOK002
     */
    @GetMapping(params = "order-books")
    public ResponseEntity<SelectedBooksResponse> getSelected(
            @RequestParam("order-books") String orderBooks) {
        return ResponseEntity.ok(SelectedBooksResponse.ok(booksService.getSelected(orderBooks)));
    }
}
