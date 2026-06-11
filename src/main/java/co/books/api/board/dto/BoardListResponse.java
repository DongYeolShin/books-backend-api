package co.books.api.board.dto;

import java.util.List;

/** 게시글 목록 응답 */
public record BoardListResponse(int code, List<BoardItemDto> data) {
    public static BoardListResponse ok(List<BoardItemDto> data) {
        return new BoardListResponse(200, data);
    }
}
