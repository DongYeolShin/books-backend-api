package co.books.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 외부 API 호출용 WebClient / 직렬화기 빈 설정.
 *
 * <p>포트원 V2 REST API 호출 전용 WebClient 와, 포트원 응답을 raw_response(JSONB) 에
 * 저장하기 위한 ObjectMapper 를 등록한다.
 * Spring Boot 4.0.x + starter-webmvc 환경에서 ObjectMapper 가 자동 구성되지 않는 경우를
 * 대비해 {@link ConditionalOnMissingBean} 으로 안전하게 보장한다.</p>
 */
@Configuration
public class WebClientConfig {

    @Bean(name = "portOneWebClient")
    public WebClient portOneWebClient(@Value("${portone.api-base-url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * 포트원 응답 직렬화/역직렬화에 사용할 ObjectMapper.
     * JavaTimeModule 을 등록해 OffsetDateTime 등 java.time 타입을 ISO-8601 로 처리한다.
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
