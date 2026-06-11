package co.books.api.board.dto;

/** 게시글 삭제 응답 */
public record BoardDeleteResponse(int code, String message) {
    public static BoardDeleteResponse ok() { return new BoardDeleteResponse(200, "삭제되었습니다."); }
    public static BoardDeleteResponse fail() { return new BoardDeleteResponse(500, "삭제 실패되었습니다."); }
    public static BoardDeleteResponse forbidden() { return new BoardDeleteResponse(403, "본인이 작성한 게시글만 삭제할 수 있습니다."); }
    public static BoardDeleteResponse notFound() { return new BoardDeleteResponse(404, "존재하지 않는 게시글입니다."); }
}
