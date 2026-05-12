package co.books.api.common.dto;

import java.time.OffsetDateTime;

/**
 * 글로벌 에러 응답 본문.
 * { code, message, timestamp } 구조로 모든 예외 응답에 공통 사용된다.
 */
public record ErrorResponse(
        int code,
        String message,
        OffsetDateTime timestamp
) {
    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(code, message, OffsetDateTime.now());
    }
}
