package co.books.api.auth.handler;

import co.books.api.auth.jwt.JwtTokenProvider;
import co.books.api.auth.service.CustomUserDetails;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 로그인 성공 시 JWT 액세스 토큰을 발급하여 JSON 으로 응답한다.
 * UsernamePasswordAuthenticationFilter 가 인증을 끝낸 후 호출된다.
 */
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String userId = authentication.getName();
        String token = jwtTokenProvider.createToken(userId);

        // CustomUserDetailsService 가 항상 CustomUserDetails 를 반환하므로 캐스팅이 안전하다.
        String name = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails details) {
            name = details.getName();
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("tokenType", "Bearer");
        body.put("accessToken", token);
        body.put("expiresIn", jwtTokenProvider.getAccessTokenValidityMillis() / 1000);
        body.put("userId", userId);
        body.put("name", name);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), body);
    }
}
