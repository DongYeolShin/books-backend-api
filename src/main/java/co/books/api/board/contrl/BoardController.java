package co.books.api.board.contrl;

import co.books.api.board.dto.BoardCreateRequest;
import co.books.api.board.dto.BoardCreateResponse;
import co.books.api.board.dto.BoardDeleteResponse;
import co.books.api.board.dto.BoardDetailDto;
import co.books.api.board.dto.BoardDetailResponse;
import co.books.api.board.dto.BoardListResponse;
import co.books.api.board.dto.BoardUpdateRequest;
import co.books.api.board.dto.BoardUpdateResponse;
import co.books.api.board.service.BoardMutationResult;
import co.books.api.board.service.BoardService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 게시판 컨트롤러. /api/v1/boards 하위 엔드포인트를 제공한다.
 */
@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;

    /** 게시글 목록 조회. 비로그인 공개. */
    @GetMapping
    public ResponseEntity<BoardListResponse> list() {
        return ResponseEntity.ok(BoardListResponse.ok(boardService.getList()));
    }

    /**
     * 게시글 단건 조회.
     * board_read 쿠키에 해당 boardId 가 없으면 read_count 를 1 증가시키고 쿠키에 추가한다.
     * 비로그인 공개.
     */
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailResponse> getDetail(
            @PathVariable Long boardId,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 먼저 게시글 존재 여부를 확인한다. 존재하지 않으면 NotFoundException 으로 404 를 반환하며,
        // 이 경우 조회수 증가나 쿠키 설정은 수행하지 않는다.
        BoardDetailDto detail = boardService.getDetail(boardId);

        String cookieValue = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("board_read".equals(c.getName())) {
                    cookieValue = c.getValue();
                    break;
                }
            }
        }

        // 쿠키에 해당 boardId 마커가 없으면 조회수 증가 후 쿠키를 갱신한다
        String marker = "[" + boardId + "]";
        if (!cookieValue.contains(marker)) {
            boardService.incrementReadCount(boardId);
            String newValue = cookieValue + marker;
            Cookie newCookie = new Cookie("board_read", newValue);
            newCookie.setMaxAge(21600);  // 6시간
            newCookie.setPath("/");
            response.addCookie(newCookie);
        }

        return ResponseEntity.ok(BoardDetailResponse.ok(detail));
    }

    /** 게시글 등록. JWT 인증 필요. writer 는 인증된 userId 로 설정된다. */
    @PostMapping
    public ResponseEntity<BoardCreateResponse> create(
            @AuthenticationPrincipal String userId,
            @RequestBody BoardCreateRequest request) {
        try {
            boardService.create(userId, request);
            return ResponseEntity.ok(BoardCreateResponse.ok());
        } catch (Exception e) {
            log.error("게시글 등록 실패: userId={}", userId, e);
            return ResponseEntity.ok(BoardCreateResponse.fail());
        }
    }

    /** 게시글 수정 (title, contents 만). JWT 인증 필요. 작성자 본인만 수정 가능. */
    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardUpdateResponse> update(
            @AuthenticationPrincipal String userId,
            @PathVariable Long boardId,
            @RequestBody BoardUpdateRequest request) {
        try {
            BoardMutationResult result = boardService.update(userId, boardId, request);
            return switch (result) {
                case OK -> ResponseEntity.ok(BoardUpdateResponse.ok());
                case NOT_FOUND -> ResponseEntity.ok(BoardUpdateResponse.notFound());
                case FORBIDDEN -> ResponseEntity.ok(BoardUpdateResponse.forbidden());
            };
        } catch (Exception e) {
            log.error("게시글 수정 실패: userId={}, boardId={}", userId, boardId, e);
            return ResponseEntity.ok(BoardUpdateResponse.fail());
        }
    }

    /** 게시글 삭제. JWT 인증 필요. 작성자 본인만 삭제 가능. */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<BoardDeleteResponse> delete(
            @AuthenticationPrincipal String userId,
            @PathVariable Long boardId) {
        try {
            BoardMutationResult result = boardService.delete(userId, boardId);
            return switch (result) {
                case OK -> ResponseEntity.ok(BoardDeleteResponse.ok());
                case NOT_FOUND -> ResponseEntity.ok(BoardDeleteResponse.notFound());
                case FORBIDDEN -> ResponseEntity.ok(BoardDeleteResponse.forbidden());
            };
        } catch (Exception e) {
            log.error("게시글 삭제 실패: userId={}, boardId={}", userId, boardId, e);
            return ResponseEntity.ok(BoardDeleteResponse.fail());
        }
    }
}
