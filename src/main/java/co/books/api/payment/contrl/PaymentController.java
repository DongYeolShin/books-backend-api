package co.books.api.payment.contrl;

import co.books.api.payment.dto.PaymentCompleteRequest;
import co.books.api.payment.dto.PaymentCompleteResponse;
import co.books.api.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 결제 관련 REST API.
 * 기본 경로: /api/v1/payments
 *
 * <p>로그인 사용자만 접근 가능하며, 결제 완료(검증) 단일 엔드포인트만 제공한다.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 완료 검증.
     * 클라이언트가 포트원 결제 결과를 알려오면 서버가 포트원 API 로 재조회·검증한다.
     */
    @PostMapping("/complete")
    public ResponseEntity<PaymentCompleteResponse> complete(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody PaymentCompleteRequest request) {
        return ResponseEntity.ok(paymentService.complete(request, userId));
    }
}
