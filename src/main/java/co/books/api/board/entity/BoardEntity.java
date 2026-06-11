package co.books.api.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 게시판 엔티티. board 테이블과 매핑된다.
 * create_at / update_at 컬럼명은 DB 스키마와 동일하게 단수형을 사용한다.
 */
@Entity
@Table(name = "board")
@Getter
@Setter
public class BoardEntity {

    /** 게시글 ID (DB 자동 채번) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    /** 제목 */
    @Column(nullable = false, length = 100)
    private String title;

    /** 작성자 (로그인 userId) */
    @Column(nullable = false, length = 50)
    private String writer;

    /** 조회수 (기본값 0) */
    @Column(name = "read_count")
    private Integer readCount = 0;

    /** 본문 (기본값 빈 문자열) */
    @Column(columnDefinition = "TEXT")
    private String contents = "";

    /** 등록 일시 (삽입 시 자동 설정, 변경 불가) */
    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    /**
     * 수정 일시. 생성 시에는 null, 수정 시에만 값이 채워진다.
     * @UpdateTimestamp 대신 서비스 계층에서 수동으로 설정한다.
     */
    @Column(name = "update_at")
    private LocalDateTime updateAt;
}
