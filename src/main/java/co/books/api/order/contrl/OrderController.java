package co.books.api.order.contrl;

import co.books.api.order.dto.CreateOrderRequest;
import co.books.api.order.dto.CreateOrderResponse;
import co.books.api.order.dto.OrderListResponse;
import co.books.api.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 주문 생성 REST API.
 * 기본 경로: /api/v1/orders
 * SecurityConfig 의 anyRequest().authenticated() 에 의해 로그인 사용자만 접근할 수 있다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성.
     * 응답 본문의 orderId 가 곧 결제 단계의 paymentId 로 사용된다.
     */
    @PostMapping
    public ResponseEntity<CreateOrderResponse> create(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request, userId));
    }

    /**
     * 본인 구매목록(주문내역) 리스트 조회.
     *
     * <p>결제완료(PAID) / 배송중(SHIPPED) / 배송완료(DELIVERED) 상태만 노출하며,
     * 주문취소는 제외한다. orderedAt 역순 정렬, 페이지당 10건 고정.</p>
     *
     * @param page 1-based 페이지 번호 (기본 1)
     */
    @GetMapping
    public ResponseEntity<OrderListResponse> getMyOrders(
            @AuthenticationPrincipal String userId,
            @RequestParam(value = "page", required = false) Integer page) {
        return ResponseEntity.ok(orderService.getMyOrders(userId, page));
    }
}
