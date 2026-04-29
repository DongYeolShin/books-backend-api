package co.books.api.book.service;

import co.books.api.book.dto.BooksDetailDto;
import co.books.api.book.dto.BooksItemDto;
import co.books.api.book.dto.BooksReviewItemDto;
import co.books.api.book.dto.BooksTopNData;
import co.books.api.book.entity.BookEntity;
import co.books.api.book.repo.BookRepository;
import co.books.api.review.repo.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
