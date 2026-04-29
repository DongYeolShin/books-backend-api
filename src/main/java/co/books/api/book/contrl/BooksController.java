package co.books.api.book.contrl;

import co.books.api.book.dto.BooksDetailResponse;
import co.books.api.book.dto.BooksTopNResponse;
import co.books.api.book.service.BooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 도서 상세 페이지 정보 (리뷰 목록 포함).
     * 인증 없이 접근 가능하다.
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BooksDetailResponse> getDetail(@PathVariable String bookId) {
        return ResponseEntity.ok(BooksDetailResponse.ok(booksService.getDetail(bookId)));
    }
}
