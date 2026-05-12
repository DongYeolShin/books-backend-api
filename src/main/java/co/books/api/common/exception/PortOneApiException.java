package co.books.api.common.exception;

/**
 * 포트원 API 호출 실패 예외. 글로벌 핸들러에서 502 로 매핑된다.
 */
public class PortOneApiException extends RuntimeException {

    public PortOneApiException(String message) {
        super(message);
    }

    public PortOneApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
