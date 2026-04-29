package co.books.api.config;

import co.books.api.auth.filter.JwtAuthenticationFilter;
import co.books.api.auth.handler.JwtAccessDeniedHandler;
import co.books.api.auth.handler.JwtAuthenticationEntryPoint;
import co.books.api.auth.handler.LoginFailureHandler;
import co.books.api.auth.handler.LoginSuccessHandler;
import co.books.api.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정.
 *
 * <ul>
 *   <li>{@code POST /api/v1/login} 은 {@link UsernamePasswordAuthenticationFilter} 가 가로채며,
 *       성공 시 {@link LoginSuccessHandler} 가 JWT 를 발급해 반환한다.</li>
 *   <li>이후 요청은 Authorization 헤더의 Bearer 토큰을 {@link JwtAuthenticationFilter} 가 검증한다.</li>
 *   <li>인증/권한 실패는 {@link JwtAuthenticationEntryPoint} / {@link JwtAccessDeniedHandler} 가 처리한다.</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    /** 로그인 처리 URL. UsernamePasswordAuthenticationFilter 가 가로채는 경로. */
    public static final String LOGIN_URL = "/api/v1/login";

    private final JwtTokenProvider jwtTokenProvider;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        // UserEntity.passwd 컬럼이 bcrypt 로 저장된다는 전제 (CLAUDE.md / 엔티티 주석 기준).
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager) throws Exception {
        // /api/v1/login 을 가로챌 기본 폼 로그인 필터를 직접 구성하여 success/failure 핸들러를 연결한다.
        UsernamePasswordAuthenticationFilter loginFilter = new UsernamePasswordAuthenticationFilter(authenticationManager);
        loginFilter.setFilterProcessesUrl(LOGIN_URL);
        loginFilter.setUsernameParameter("userId");
        loginFilter.setPasswordParameter("passwd");
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        loginFilter.setAuthenticationFailureHandler(loginFailureHandler);

        http
                // REST API + JWT 환경에서는 CSRF, 폼 로그인 페이지, 세션을 모두 비활성화한다.
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // 로그인/회원가입 등 공개 엔드포인트
                        .requestMatchers(LOGIN_URL, "/users/signup").permitAll()
                        // 도서 조회 엔드포인트 (메인 Top-N, 상세 등) 는 비로그인 접근 허용
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/**").permitAll()
                        // 임시: 기존 학습용 엔드포인트는 공개
                        .requestMatchers("/test", "/std/list").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                // /login 처리용 폼 로그인 필터를 명시적으로 등록한다.
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                // JWT 검증 필터는 폼 로그인 필터보다 앞에 둔다.
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
