package co.books.api.board.dto;

/** 게시글 등록 요청 DTO */
public record BoardCreateRequest(String title, String contents) {}
