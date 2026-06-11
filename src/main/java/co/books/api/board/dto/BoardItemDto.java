package co.books.api.board.dto;

import java.time.LocalDateTime;

/** 게시글 목록 항목 DTO (본문 제외) */
public record BoardItemDto(Long boardId, String title, String writer, int readCount, LocalDateTime createAt) {}
