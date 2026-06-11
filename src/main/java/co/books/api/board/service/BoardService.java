package co.books.api.board.service;

import co.books.api.board.dto.BoardCreateRequest;
import co.books.api.board.dto.BoardDetailDto;
import co.books.api.board.dto.BoardItemDto;
import co.books.api.board.dto.BoardUpdateRequest;
import co.books.api.board.entity.BoardEntity;
import co.books.api.board.repo.BoardRepository;
import co.books.api.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시판 서비스. 게시글 CRUD 및 조회수 증가 로직을 제공한다.
 */
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     * 전체 게시글 목록을 최신 등록순으로 조회한다.
     */
    @Transactional(readOnly = true)
    public List<BoardItemDto> getList() {
        return boardRepository.findAllByOrderByBoardIdDesc().stream()
                .map(e -> new BoardItemDto(
                        e.getBoardId(),
                        e.getTitle(),
                        e.getWriter(),
                        e.getReadCount(),
                        e.getCreateAt()
                ))
                .toList();
    }

    /**
     * 게시글 단건을 조회한다. 존재하지 않으면 NotFoundException 을 던진다.
     */
    @Transactional(readOnly = true)
    public BoardDetailDto getDetail(Long boardId) {
        BoardEntity entity = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        return new BoardDetailDto(
                entity.getBoardId(),
                entity.getTitle(),
                entity.getWriter(),
                entity.getReadCount(),
                entity.getContents(),
                entity.getCreateAt(),
                entity.getUpdateAt()
        );
    }

    /**
     * read_count 를 원자적으로 1 증가시킨다. 단건 조회보다 먼저 호출된다.
     */
    @Transactional
    public void incrementReadCount(Long boardId) {
        boardRepository.incrementReadCount(boardId);
    }

    /**
     * 게시글을 등록한다. writer 는 인증된 userId 로 설정된다.
     */
    @Transactional
    public void create(String userId, BoardCreateRequest req) {
        BoardEntity entity = new BoardEntity();
        entity.setTitle(req.title());
        entity.setContents(req.contents());
        entity.setWriter(userId);
        boardRepository.save(entity);
    }

    /**
     * 게시글 제목과 본문을 수정한다.
     * 존재하지 않으면 NOT_FOUND, 작성자가 아니면 FORBIDDEN, 성공하면 OK 를 반환한다.
     */
    @Transactional
    public BoardMutationResult update(String userId, Long boardId, BoardUpdateRequest req) {
        BoardEntity entity = boardRepository.findById(boardId).orElse(null);
        if (entity == null) {
            return BoardMutationResult.NOT_FOUND;
        }
        if (!entity.getWriter().equals(userId)) {
            return BoardMutationResult.FORBIDDEN;
        }
        entity.setTitle(req.title());
        entity.setContents(req.contents());
        entity.setUpdateAt(LocalDateTime.now());
        // dirty checking 으로 자동 UPDATE 처리
        return BoardMutationResult.OK;
    }

    /**
     * 게시글을 삭제한다.
     * 존재하지 않으면 NOT_FOUND, 작성자가 아니면 FORBIDDEN, 성공하면 OK 를 반환한다.
     */
    @Transactional
    public BoardMutationResult delete(String userId, Long boardId) {
        BoardEntity entity = boardRepository.findById(boardId).orElse(null);
        if (entity == null) {
            return BoardMutationResult.NOT_FOUND;
        }
        if (!entity.getWriter().equals(userId)) {
            return BoardMutationResult.FORBIDDEN;
        }
        boardRepository.delete(entity);
        return BoardMutationResult.OK;
    }
}
