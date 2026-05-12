package co.books.api.payment.service;

import co.books.api.common.exception.PortOneApiException;
import co.books.api.payment.dto.PortOnePaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * 포트원 V2 결제 단건 조회 API 클라이언트.
 *
 * <p>인증 스킴은 PortOne (V2 권장) 을 사용한다.
 * Bearer 가 아니므로 {@code Authorization: PortOne <api-secret>} 형식으로 보낸다.</p>
 */
@Slf4j
@Component
public class PortOneApiClient {

    private final WebClient portOneWebClient;
    private final String apiSecret;

    public PortOneApiClient(
            @Qualifier("portOneWebClient") WebClient portOneWebClient,
            @Value("${portone.api-secret}") String apiSecret) {
        this.portOneWebClient = portOneWebClient;
        this.apiSecret = apiSecret;
    }

    /**
     * paymentId 로 포트원에 결제 단건 조회를 수행한다.
     * 4xx/5xx 응답은 {@link PortOneApiException} 으로 래핑되어 던져진다.
     */
    public PortOnePaymentResponse getPayment(String paymentId) {
        if (apiSecret == null || apiSecret.isBlank()) {
            throw new PortOneApiException("포트원 API Secret 이 설정되어 있지 않습니다. (PORTONE_API_SECRET)");
        }
        try {
            return portOneWebClient.get()
                    .uri("/payments/{paymentId}", paymentId)
                    .header(HttpHeaders.AUTHORIZATION, "PortOne " + apiSecret)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, res -> res.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(body -> Mono.error(new PortOneApiException(
                                    "포트원 결제 조회 실패 (status=" + res.statusCode().value() + ", body=" + body + ")"))))
                    .bodyToMono(PortOnePaymentResponse.class)
                    .block();
        } catch (PortOneApiException e) {
            throw e;
        } catch (Exception e) {
            throw new PortOneApiException("포트원 API 호출 중 오류: " + e.getMessage(), e);
        }
    }
}
