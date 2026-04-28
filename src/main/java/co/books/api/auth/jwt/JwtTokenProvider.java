package co.books.api.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 토큰 발급/파싱/검증 컴포넌트.
 * 시크릿 키는 애플리케이션 시작 시 {@link Keys#secretKeyFor} 로 임의 생성하여 메모리에만 보관한다.
 * 따라서 재시작 시 기존 토큰은 모두 무효화되며, 다중 인스턴스 환경에는 적합하지 않다.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    /** 액세스 토큰 유효 시간 (밀리초 단위) */
    @Value("${jwt.access-token-validity}")
    private long accessTokenValidityMillis;

    /** 토큰 issuer 식별자 */
    @Value("${jwt.issuer}")
    private String issuer;

    /** HMAC-SHA256 서명용 시크릿 키 (애플리케이션 시작 시 1회 생성) */
    private SecretKey signingKey;

    @PostConstruct
    void init() {
        // HS256 용 256비트 임의 키 생성. 운영용 다중 인스턴스 환경에서는 외부 주입 방식으로 전환 필요.
        this.signingKey = Jwts.SIG.HS256.key().build();
        log.info("JWT 시크릿 키 초기화 완료 (재시작 시 기존 토큰은 모두 무효화됨)");
    }

    /**
     * 사용자 식별자(userId)를 subject 로 하는 JWT 액세스 토큰을 생성한다.
     *
     * @param userId 토큰의 subject 로 사용할 회원 ID
     * @return 서명된 JWT 문자열
     */
    public String createToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidityMillis);

        return Jwts.builder()
                .issuer(issuer)
                .subject(userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 토큰의 서명/만료/형식이 모두 유효한지 검증한다.
     *
     * @param token JWT 문자열
     * @return 유효 여부
     */
    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰입니다. {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("유효하지 않은 JWT 토큰입니다. {}", e.getMessage());
        }
        return false;
    }

    /**
     * 토큰에서 subject(userId) 를 추출한다.
     * 검증을 통과한 토큰에 대해서만 호출해야 한다.
     */
    public String getUserId(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 액세스 토큰 유효 시간(ms) 반환.
     */
    public long getAccessTokenValidityMillis() {
        return accessTokenValidityMillis;
    }

    /**
     * 토큰을 파싱하여 Claims 를 반환한다.
     * 만료/위변조/형식 오류 시 JwtException 계열 예외를 던진다.
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
