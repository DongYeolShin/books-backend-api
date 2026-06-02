package co.books.api.user.dto;

/**
 * 회원가입 API 응답 본문.
 * 성공/실패 모두 {@code { code, message }} 구조로 반환한다.
 */
public record SignupResponse(
        int code,
        String message
) {
    public static SignupResponse ok() {
        return new SignupResponse(200, "회원가입이 완료되었습니다.");
    }

    public static SignupResponse fail() {
        return new SignupResponse(500, "회원가입에 실패했습니다.");
    }
}
