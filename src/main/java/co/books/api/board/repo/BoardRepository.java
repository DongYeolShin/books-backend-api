package co.books.api.board.repo;

import co.books.api.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시판 리포지토리.
 */
@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    /** 게시글 목록을 최신 등록순(board_id 내림차순)으로 조회한다. */
    List<BoardEntity> findAllByOrderByBoardIdDesc();

    /** read_count 를 원자적으로 1 증가시킨다. 동시 요청에서 race condition 방지. */
    @Modifying
    @Query(value = "UPDATE board SET read_count = read_count + 1 WHERE board_id = :boardId", nativeQuery = true)
    int incrementReadCount(@Param("boardId") Long boardId);
}
