package co.books.api.board.dto;

/** 게시글 등록 응답 */
public record BoardCreateResponse(int code, String message) {
    public static BoardCreateResponse ok() { return new BoardCreateResponse(200, "게시글이 등록되었습니다."); }
    public static BoardCreateResponse fail() { return new BoardCreateResponse(500, "게시글 등록에 실패했습니다."); }
}
