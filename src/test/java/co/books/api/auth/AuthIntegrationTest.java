package co.books.api.auth;

import co.books.api.user.entity.UserEntity;
import co.books.api.user.repo.UserRepository;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.web.FilterChainProxy;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 로그인 / JWT 보호 리소스 접근 / 401 / 403 동작을 MockMvc 로 검증한다.
 * UserRepository 는 모킹하여 실제 DB 접근 없이 인증 흐름만 검증한다.
 */
@SpringBootTest
class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    private MockMvc mockMvc;

    private static final String TEST_USER_ID = "tester";
    private static final String TEST_RAW_PW = "secret123!";

    @BeforeEach
    void setUp() {
        // SecurityFilterChain 을 직접 끼워 넣어 실제 인증 흐름을 그대로 태운다.
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(springSecurityFilterChain)
                .build();

        UserEntity user = new UserEntity();
        user.setUserId(TEST_USER_ID);
        user.setEmail("tester@example.com");
        user.setPasswd(passwordEncoder.encode(TEST_RAW_PW));
        user.setName("테스터");

        given(userRepository.findById(TEST_USER_ID)).willReturn(Optional.of(user));
    }

    @Test
    void 로그인_성공시_액세스_토큰을_반환한다() throws Exception {
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("userId", TEST_USER_ID)
                        .param("passwd", TEST_RAW_PW))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.name").value("테스터"));
    }

    @Test
    void 로그인_실패시_401_을_반환한다() throws Exception {
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("userId", TEST_USER_ID)
                        .param("passwd", "wrong-password"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void 토큰_없이_보호_리소스에_접근하면_401_을_반환한다() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void 발급받은_토큰으로_보호_리소스에_접근할_수_있다() throws Exception {
        // 1) 로그인하여 토큰 발급
        MvcResult loginResult = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("userId", TEST_USER_ID)
                        .param("passwd", TEST_RAW_PW))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = body.get("accessToken").asText();
        assertThat(accessToken).isNotBlank();

        // 2) 토큰으로 보호 리소스에 접근.
        // 보호 엔드포인트가 아직 없으므로 존재하지 않는 경로로 요청한다.
        // 인증은 통과하고 404 가 반환되면 토큰이 유효하다는 의미이다.
        mockMvc.perform(get("/api/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void 위변조된_토큰으로_접근하면_401_을_반환한다() throws Exception {
        mockMvc.perform(get("/api/me")
                        .header("Authorization", "Bearer this.is.broken"))
                .andExpect(status().isUnauthorized());
    }
}
