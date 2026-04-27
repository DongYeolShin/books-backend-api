---
name: spring-boot-jwt
description: Spring Boot 프로젝트에서 Spring Security 기본 폼 로그인 흐름 위에 JWT 인증을 얹을 때 사용한다. 별도 로그인 컨트롤러를 만들지 않고 UsernamePasswordAuthenticationFilter를 그대로 쓰면서, AuthenticationSuccessHandler에서 토큰을 발급하고 JwtAuthenticationFilter에서 토큰을 검증한다. 프론트엔드가 분리된 SPA/모바일 환경을 가정한다. 사용자가 'JWT', '토큰 인증', 'Spring Security', 'JwtAuthenticationFilter', 'SuccessHandler', 'FailureHandler', 'AuthenticationEntryPoint', '로그인 핸들러', 'JJWT'를 언급하거나, Spring Boot에 JWT 인증을 추가하려 할 때 반드시 이 스킬을 사용한다. 코드 작성 전에 프로젝트의 Spring Boot 버전을 build.gradle/pom.xml에서 확인하고(2.x와 3.x는 SecurityConfig 문법이 다름), JJWT는 항상 Maven Central의 최신 버전(0.12.x 이상)을 사용한다. 테스트 코드(MockMvc 로그인 성공/실패, JwtAuthenticationFilter 단위 테스트)도 함께 작성한다.
---

# Spring Boot JWT 인증 (Filter + Handler 방식)

Spring Security 기본 폼 로그인 흐름을 그대로 사용하면서, 인증 성공 시점에 JWT를 발급하고 이후 요청은 JWT로 검증한다. 별도 로그인 컨트롤러는 만들지 않는다.

## 작성 원칙

1. 별도 로그인 컨트롤러를 만들지 않는다. `/login`은 `UsernamePasswordAuthenticationFilter`가 가로챈다.
2. 토큰 발급/검증/예외 처리는 모두 Filter와 Handler에서 처리한다. Service 레이어로 빼지 않는다.
3. 코드 작성 전 `build.gradle` 또는 `pom.xml`에서 Spring Boot 버전을 확인하고 그에 맞는 SecurityConfig 문법을 사용한다.
    - 3.x: `jakarta.*`, `SecurityFilterChain` Bean 방식
    - 2.x: `javax.*`, `WebSecurityConfigurerAdapter` 상속 방식
4. JJWT는 Maven Central에서 최신 버전을 확인해 사용한다. 0.12.x API(`Jwts.parser().verifyWith().build().parseSignedClaims()`)를 쓰며, 0.11.x의 옛 API(`parserBuilder`, `setSigningKey`, `parseClaimsJws`)는 사용하지 않는다.
5. 구현 후 테스트 코드를 반드시 함께 작성한다.

## 전체 흐름

```
[로그인]
POST /login → UsernamePasswordAuthenticationFilter
              ├─ 성공 → LoginSuccessHandler → JWT 발급 → JSON 응답
              └─ 실패 → LoginFailureHandler → 401 JSON 응답

[인증된 요청]
Authorization: Bearer xxx → JwtAuthenticationFilter
                            ├─ 유효 → SecurityContext에 Authentication 주입 → Controller
                            └─ 무효/없음 → SecurityContext 비움 → 보호 리소스 접근 시 EntryPoint가 401

[권한 부족] → JwtAccessDeniedHandler → 403
```

## 의존성 (build.gradle)

```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-web'

// JJWT 최신 버전 (Maven Central에서 재확인)
implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
runtimeOnly  'io.jsonwebtoken:jjwt-impl:0.12.6'
runtimeOnly  'io.jsonwebtoken:jjwt-jackson:0.12.6'

testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.springframework.security:spring-security-test'
```

## application.yml

시크릿 키는 설정 파일에 두지 않고 애플리케이션 시작 시 임의로 생성한다.

```yaml
jwt:
  access-token-validity: 1800000     # 30분 (ms)
  issuer: your-service-name
```

## 시크릿 키 임의 생성

`JwtTokenProvider`의 `@PostConstruct`에서 `Keys.secretKeyFor(Jwts.SIG.HS256)`로 256비트 키를 생성해 메모리에만 보관한다.

```java
@PostConstruct
void init() {
    this.signingKey = Keys.secretKeyFor(Jwts.SIG.HS256);
}
```

**특성과 주의점**

- 애플리케이션이 재시작되면 키가 새로 생성되므로 기존에 발급된 토큰은 모두 무효화된다. 사용자는 다시 로그인해야 한다. 이 동작이 의도된 경우에만 사용한다.
- 다중 인스턴스 환경(스케일 아웃, 무중단 배포)에서는 인스턴스마다 키가 달라 토큰이 호환되지 않는다. 단일 인스턴스 또는 개발/테스트 환경 전용이다.
- 운영 환경에서 인스턴스 간 토큰을 공유해야 한다면 환경변수나 비밀 관리 시스템(Vault, AWS Secrets Manager 등)으로 고정 키를 주입하는 방식으로 전환한다.

## 작성 순서

1. **JwtTokenProvider** — 토큰 생성/파싱/검증
2. **LoginSuccessHandler** (`AuthenticationSuccessHandler` 구현) — 인증 성공 시 토큰을 만들어 JSON 응답
3. **LoginFailureHandler** (`AuthenticationFailureHandler` 구현) — 인증 실패 시 401 JSON 응답
4. **JwtAuthenticationFilter** (`OncePerRequestFilter` 상속) — Authorization 헤더 검증 후 SecurityContext 주입
5. **JwtAuthenticationEntryPoint** (`AuthenticationEntryPoint` 구현) — 미인증 401 응답
6. **JwtAccessDeniedHandler** (`AccessDeniedHandler` 구현) — 권한 부족 403 응답
7. **SecurityConfig** — 위 컴포넌트를 필터 체인에 등록
8. **테스트 코드** — MockMvc 통합 테스트 + JwtAuthenticationFilter 단위 테스트

## 핵심 포인트

### 로그인 처리
`UsernamePasswordAuthenticationFilter`가 username/password를 받아 `AuthenticationManager` → `UserDetailsService`로 인증한다. **이 흐름은 Spring Security 기본 제공이므로 코드를 작성하지 않는다.** 결과만 핸들러로 받는다.

SecurityConfig에서 `formLogin()` 또는 `UsernamePasswordAuthenticationFilter`를 명시적으로 등록하고, `successHandler`/`failureHandler`를 연결한다.

### 토큰 검증
`JwtAuthenticationFilter`는 `OncePerRequestFilter`를 상속한다. `Authorization: Bearer xxx`에서 공백 포함 7글자(`"Bearer "`)를 `substring(7)`로 제거한다. 토큰이 없거나 무효해도 **Filter에서 직접 401을 내려보내지 않는다.** SecurityContext를 비워둔 채 다음 필터로 넘기면, 보호 리소스 접근 시 Spring Security가 `AuthenticationEntryPoint`를 호출한다.

### SecurityConfig 필수 설정
- `csrf().disable()` — REST API에서 CSRF 비활성화
- `sessionCreationPolicy(STATELESS)` — JSESSIONID 발급 방지
- `JwtAuthenticationFilter`를 `UsernamePasswordAuthenticationFilter` 앞에 등록 (`addFilterBefore`)
- `exceptionHandling`에 EntryPoint와 AccessDeniedHandler 연결

### 테스트
- 로그인 성공: `MockMvc.perform(post("/login")...)` → 응답에 토큰 존재 확인
- 로그인 실패: 잘못된 비밀번호 → 401 확인
- 보호 엔드포인트: 토큰 없이 요청 → 401, 유효 토큰 → 200, 만료 토큰 → 401
- `JwtAuthenticationFilter` 단위 테스트: `MockHttpServletRequest`/`MockHttpServletResponse`로 헤더 파싱, SecurityContext 주입 검증
- 토큰 생성/검증 단위 테스트: `JwtTokenProvider`의 `createToken`, `validate`, `getUserId`를 각각 검증

## 자주 하는 실수

- 로그인 컨트롤러를 또 만든다 → `/login`은 Filter가 가로채므로 컨트롤러가 호출되지 않거나 충돌
- `Bearer ` 접두사 제거 누락 (공백 포함 7글자)
- Filter에서 예외를 throw → `@ControllerAdvice`로 잡히지 않음. EntryPoint/AccessDeniedHandler로 처리
- `csrf().disable()`, `SessionCreationPolicy.STATELESS` 누락
- JJWT 0.11.x 문법 사용 → 항상 최신 API 사용
- 시크릿 키를 application.yml에 평문 하드코딩 → 임의 생성 방식 사용
- 다중 인스턴스 환경에서 임의 생성 키 사용 → 인스턴스 간 토큰 불일치 발생, 고정 키 주입 방식으로 전환
- `JwtAuthenticationFilter`를 `@Component`로만 두고 SecurityConfig에 등록하지 않음 → 동작하지 않음
