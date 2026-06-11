package co.books.api.board.dto;

/** 게시글 단건 조회 응답 */
public record BoardDetailResponse(int code, BoardDetailDto data) {
    public static BoardDetailResponse ok(BoardDetailDto data) {
        return new BoardDetailResponse(200, data);
    }
}
