package co.books.api.common.exception;

/**
 * 리소스를 찾지 못한 경우의 예외. 글로벌 핸들러에서 404 로 매핑된다.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
