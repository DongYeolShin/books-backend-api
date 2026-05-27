package co.books.api.user.contrl;

import co.books.api.user.dto.MyPageResponse;
import co.books.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 관련 REST API.
 * 기본 경로: /api/v1/users
 *
 * <p>로그인 사용자만 접근 가능하다.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 마이페이지 조회.
     * 인증된 사용자 본인의 정보와 최근 주문 목록을 반환한다.
     */
    @GetMapping("/me")
    public ResponseEntity<MyPageResponse> getMyPage(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(userService.getMyPage(userId));
    }
}
