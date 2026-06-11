package co.books.api.board.service;

/**
 * 게시글 수정·삭제 결과를 나타내는 열거형.
 * 컨트롤러가 이 값에 따라 적절한 응답 DTO 팩토리를 선택한다.
 */
public enum BoardMutationResult {
    /** 처리 성공 */
    OK,
    /** 해당 게시글이 존재하지 않음 */
    NOT_FOUND,
    /** 작성자가 아닌 사용자가 수정·삭제 시도 */
    FORBIDDEN
}
