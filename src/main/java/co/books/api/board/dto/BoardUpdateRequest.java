package co.books.api.board.dto;

/** 게시글 수정 요청 DTO */
public record BoardUpdateRequest(String title, String contents) {}
