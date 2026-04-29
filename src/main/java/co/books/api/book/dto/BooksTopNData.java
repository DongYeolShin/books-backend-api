package co.books.api.book.dto;

import java.util.List;

/**
 * 메인 페이지 Top-N 응답의 data 필드.
 * 베스트셀러 / 신간 / 기본서 묶음을 각각 5 개씩 담는다.
 */
public record BooksTopNData(
        List<BooksItemDto> bestTopN,
        List<BooksItemDto> newTopN,
        List<BooksItemDto> basicTopN
) {
}
