package co.books.api.user.dto;

/**
 * 아이디 중복확인 API 응답 본문.
 * {@code available} 이 {@code true} 면 사용 가능, {@code false} 면 이미 사용 중임을 나타낸다.
 */
public record CheckUserIdResponse(
        int code,
        boolean available,
        String message
) {
    /** 사용 가능한 아이디인 경우 반환할 응답. */
    public static CheckUserIdResponse ofAvailable() {
        return new CheckUserIdResponse(200, true, "사용 가능한 아이디입니다.");
    }

    /** 이미 사용 중인 아이디인 경우 반환할 응답. */
    public static CheckUserIdResponse ofTaken() {
        return new CheckUserIdResponse(200, false, "이미 사용 중인 아이디입니다.");
    }
}
