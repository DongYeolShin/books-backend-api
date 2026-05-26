package co.books.api.payment.contrl;

import co.books.api.payment.dto.OrderCompleteResponse;
import co.books.api.payment.dto.PaymentCancelRequest;
import co.books.api.payment.dto.PaymentCancelResponse;
import co.books.api.payment.dto.PaymentCompleteRequest;
import co.books.api.payment.dto.PaymentCompleteResponse;
import co.books.api.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 결제 관련 REST API.
 * 기본 경로: /api/v1/payments
 *
 * <p>로그인 사용자만 접근 가능하다.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 주문 완료 페이지 조회.
     * orderId 에 해당하는 주문·결제·배송·주문자 정보를 반환한다.
     */
    @GetMapping("/complete")
    public ResponseEntity<OrderCompleteResponse> getOrderComplete(
            @AuthenticationPrincipal String userId,
            @RequestParam String orderId) {
        return ResponseEntity.ok(paymentService.getOrderComplete(orderId, userId));
    }

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

    /**
     * 결제 취소 (전액 또는 부분).
     * 본인 결제만 취소 가능하다.
     * cancelAmount 가 없으면 전액 취소, 있으면 부분 취소로 처리한다.
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentCancelResponse> cancel(
            @AuthenticationPrincipal String userId,
            @PathVariable String paymentId,
            @Valid @RequestBody PaymentCancelRequest request) {
        return ResponseEntity.ok(paymentService.cancel(paymentId, request, userId));
    }
}
