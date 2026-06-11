package co.books.api.board.dto;

/** 게시글 수정 응답 */
public record BoardUpdateResponse(int code, String message) {
    public static BoardUpdateResponse ok() { return new BoardUpdateResponse(200, "수정되었습니다."); }
    public static BoardUpdateResponse fail() { return new BoardUpdateResponse(500, "수정에 실패했습니다."); }
    public static BoardUpdateResponse forbidden() { return new BoardUpdateResponse(403, "본인이 작성한 게시글만 수정할 수 있습니다."); }
    public static BoardUpdateResponse notFound() { return new BoardUpdateResponse(404, "존재하지 않는 게시글입니다."); }
}
