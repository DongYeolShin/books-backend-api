package co.books.api.auth.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JwtTokenProvider 단위 테스트.
 * Spring 컨텍스트 없이 ReflectionTestUtils 로 필드를 채워 동작을 검증한다.
 */
class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "accessTokenValidityMillis", 1_800_000L);
        ReflectionTestUtils.setField(tokenProvider, "issuer", "books-backend-api-test");
        // @PostConstruct 가 자동 호출되지 않으므로 수동으로 호출한다.
        ReflectionTestUtils.invokeMethod(tokenProvider, "init");
    }

    @Test
    void createToken_과_검증이_정상_동작한다() {
        String token = tokenProvider.createToken("user-001");

        assertThat(token).isNotBlank();
        assertThat(tokenProvider.validate(token)).isTrue();
        assertThat(tokenProvider.getUserId(token)).isEqualTo("user-001");
    }

    @Test
    void 잘못된_형식의_토큰은_validate_가_false_를_반환한다() {
        assertThat(tokenProvider.validate("this.is.not.a.jwt")).isFalse();
        assertThat(tokenProvider.validate("")).isFalse();
    }

    @Test
    void 만료된_토큰은_validate_가_false_를_반환한다() {
        // 만료 시간을 음수로 설정하여 즉시 만료된 토큰을 발급한다.
        ReflectionTestUtils.setField(tokenProvider, "accessTokenValidityMillis", -1_000L);
        String expired = tokenProvider.createToken("user-001");

        assertThat(tokenProvider.validate(expired)).isFalse();
    }

    @Test
    void 다른_키로_서명된_토큰은_validate_가_false_를_반환한다() {
        String token = tokenProvider.createToken("user-001");

        // 새로운 인스턴스는 새 키를 가지므로 같은 토큰을 검증할 수 없다.
        JwtTokenProvider another = new JwtTokenProvider();
        ReflectionTestUtils.setField(another, "accessTokenValidityMillis", 1_800_000L);
        ReflectionTestUtils.setField(another, "issuer", "books-backend-api-test");
        ReflectionTestUtils.invokeMethod(another, "init");

        assertThat(another.validate(token)).isFalse();
    }
}
