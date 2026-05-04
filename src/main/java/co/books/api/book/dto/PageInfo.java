package co.books.api.book.dto;

/**
 * 페이징 결과 메타 정보.
 * nowPageNum 은 1 부터 시작하는 현재 페이지 번호, totalRows 는 전체 결과 행 수이다.
 */
public record PageInfo(
        int nowPageNum,
        long totalRows
) {
}
