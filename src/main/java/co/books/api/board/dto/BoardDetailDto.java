package co.books.api.board.dto;

import java.time.LocalDateTime;

/** 게시글 단건 조회 DTO (본문 포함) */
public record BoardDetailDto(Long boardId, String title, String writer, int readCount, String contents, LocalDateTime createAt, LocalDateTime updateAt) {}
