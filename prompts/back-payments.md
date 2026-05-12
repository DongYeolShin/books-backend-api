# 포트원 V2 일반결제 - Spring Boot 백엔드

## 컨텍스트
React 클라이언트(OrderPage)와 연동되는 포트원 V2 일반결제 백엔드.
**웹훅 미사용** — 클라이언트가 결제 결과를 알리면 서버가 포트원 REST API로 직접 검증 후 DB 저장.

## API 흐름
```
[1] POST /api/v1/orders             → orders, order_items INSERT → orderId 발급
[2] POST /api/v1/payments/complete  → GET https://api.portone.io/payments/{paymentId}
                                     → 금액·상태 검증 → payments INSERT, orders.status 갱신
```

## 클라이언트 요청/응답 명세

### `POST /api/v1/orders`
```json
// Request
{
  "receiver": "홍길동",
  "phone": "010-1234-5678",
  "shippingAddress": "[06234] 서울시 강남구 테헤란로 123",
  "shippingDetailAddress": "10층 1001호",
  "usedPoints": 5000,
  "items": [{ "bookId": "book-001", "quantity": 2, "priceAtPurchase": 15000 }]
}
// Response
{ "orderId": "ord_20260512_a1b2c3d4", "totalAmount": 25000, "orderName": "토지 외 1건" }
```

### `POST /api/v1/payments/complete`
```json
// Request
{ "paymentId": "ord_20260512_a1b2c3d4", "orderId": "ord_20260512_a1b2c3d4" }
// Response
{ "status": "PAID", "paymentId": "...", "message": "결제가 완료되었습니다." }
```

## 환경
- Spring Boot 3.x, Java 17+, JPA, PostgreSQL, Lombok, WebClient
- `application.yml`:
  ```yaml
  portone:
    api-base-url: https://api.portone.io
    api-secret: ${PORTONE_API_SECRET}
  ```
## DB-  ORDER 관련 테이블은  scripts/books_schema.sql 파일 참고 
## DB - payments 테이블 (신규)
```sql
CREATE TYPE payment_status AS ENUM ('paid', 'failed', 'virtual_account_issued');
CREATE TYPE pay_method AS ENUM ('card', 'virtual_account', 'transfer', 'mobile', 'easy_pay');

CREATE TABLE payments (
    payment_id    VARCHAR(100)   PRIMARY KEY,
    order_id      VARCHAR(100)   NOT NULL REFERENCES orders(order_id),
    user_id       VARCHAR(100)   NOT NULL REFERENCES users(user_id),
    tx_id         VARCHAR(100),
    channel_key   VARCHAR(100)   NOT NULL,
    pg_provider   VARCHAR(50),
    status        payment_status NOT NULL,
    pay_method    pay_method,
    total_amount  INTEGER        NOT NULL CHECK (total_amount >= 0),
    currency      VARCHAR(10)    NOT NULL DEFAULT 'KRW',
    order_name    VARCHAR(200)   NOT NULL,
    fail_reason   TEXT,
    fail_code     VARCHAR(50),
    raw_response  JSONB,
    paid_at       TIMESTAMPTZ,
    created_at    TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_user  ON payments(user_id);
```

## 구현 요구사항

### 1. OrderService.createOrder()
1. `@Valid` 입력 검증, 인증 사용자 `userId` 추출(SecurityContext)
2. 포인트 검증 (`users.points >= usedPoints`)
3. `books` 테이블에서 모든 `bookId` 조회 — 없으면 400
4. **금액 재계산 (★클라이언트 값 무시)**:
    - `productTotal = sum(quantity × books.sale_price)`
    - `totalAmount = productTotal - usedPoints`
5. `orderId` = `"ord_" + yyyyMMdd + "_" + UUID 8자리`
6. `orderName` = 단건이면 책 제목, 다건이면 `"{첫 책 제목} 외 {N-1}건"`
7. `@Transactional`로 묶어서:
    - `orders` INSERT (`status='pending'`)
    - `order_items` INSERT
    - `users.points` 차감 (포인트 사용 시)
8. 반환: `{ orderId, totalAmount, orderName }`

### 2. PaymentService.complete()
```java
@Transactional
public PaymentCompleteResponse complete(PaymentCompleteRequest req, String userId) {
    // 1. paymentId == orderId 검증
    if (!req.getPaymentId().equals(req.getOrderId()))
        throw new IllegalArgumentException("paymentId와 orderId 불일치");

    // 2. 멱등성 - 이미 처리된 결제면 기존 결과 반환
    Optional<Payment> exists = paymentRepository.findById(req.getPaymentId());
    if (exists.isPresent()) return PaymentCompleteResponse.from(exists.get());

    // 3. 주문 조회 + 본인 확인
    Order order = orderRepository.findById(req.getOrderId())
        .orElseThrow(() -> new NotFoundException("주문 없음"));
    if (!order.getUserId().equals(userId))
        throw new AccessDeniedException("본인의 주문이 아님");

    // 4. 포트원 API 조회
    PortOnePaymentResponse pg = portOneApiClient.getPayment(req.getPaymentId());

    // 5. 금액 검증 (★위변조 방지)
    if (pg.getAmount().getTotal() != order.getTotalAmount())
        throw new IllegalStateException("결제 금액 불일치");

    // 6. payments INSERT (raw_response에 응답 원본 저장)
    Payment payment = Payment.from(order, pg, objectMapper);
    paymentRepository.save(payment);

    // 7. orders.status 갱신
    order.updateStatus(mapToOrderStatus(pg.getStatus()));

    // 8. FAILED일 때 포인트 환불
    if ("FAILED".equals(pg.getStatus()) && order.getUsedPoints() > 0) {
        userService.refundPoints(userId, order.getUsedPoints());
    }
    return PaymentCompleteResponse.from(payment);
}

private OrderStatus mapToOrderStatus(String s) {
    return switch (s) {
        case "PAID" -> OrderStatus.PAID;
        case "FAILED" -> OrderStatus.FAILED;
        case "VIRTUAL_ACCOUNT_ISSUED" -> OrderStatus.PENDING;
        default -> OrderStatus.PENDING;
    };
}
```

### 3. PortOneApiClient
```java
@Component
@RequiredArgsConstructor
public class PortOneApiClient {
    private final WebClient portOneWebClient;
    @Value("${portone.api-secret}") private String apiSecret;

    public PortOnePaymentResponse getPayment(String paymentId) {
        return portOneWebClient.get()
            .uri("/payments/{paymentId}", paymentId)
            .header(HttpHeaders.AUTHORIZATION, "PortOne " + apiSecret)
            .retrieve()
            .onStatus(HttpStatusCode::isError, res ->
                res.bodyToMono(String.class)
                   .map(b -> new PortOneApiException("PortOne 오류: " + b)))
            .bodyToMono(PortOnePaymentResponse.class)
            .block();
    }
}
```

### 4. PortOnePaymentResponse DTO
포트원 V2 응답에서 필요한 필드만 매핑. 전체 응답은 `raw_response` JSONB에 저장.
```java
@Getter @NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortOnePaymentResponse {
    private String status;          // PAID|FAILED|VIRTUAL_ACCOUNT_ISSUED|...
    private String transactionId;
    private Channel channel;        // { key, pgProvider }
    private Method method;          // { type: "PaymentMethodCard"|"PaymentMethodEasyPay"... }
    private Amount amount;          // { total, taxFree, vat, currency }
    private String orderName;
    private OffsetDateTime paidAt;
    private Failure failure;        // { reason, pgCode }
    // 중첩 클래스 동일 패턴으로 정의
}
```

### 5. 예외 처리 (`@RestControllerAdvice`)
| 예외 | 상태코드 |
|---|---|
| `IllegalArgumentException` | 400 |
| `AccessDeniedException` | 403 |
| `NotFoundException` | 404 |
| `IllegalStateException` | 409 |
| `PortOneApiException` | 502 |
| 기타 | 500 |

응답: `{ "code", "message", "timestamp" }`

## 보안 체크리스트
- [ ] `PORTONE_API_SECRET`은 환경변수, 로그 출력 금지
- [ ] 두 API 모두 로그인 필수, `userId`는 SecurityContext에서만
- [ ] 본인 주문 검증 (`order.user_id == userId`)
- [ ] 금액은 서버에서 재계산 (`books` 테이블 기준)
- [ ] 결제 금액 검증 (`orders.total_amount` ↔ 포트원 `amount.total`)
- [ ] 멱등성 보장 (PK 충돌로 중복 INSERT 방지)

## 테스트 시나리오
1. 카드 정상 결제 → `payments.status=paid`, `orders.status=paid`
2. 결제 실패 → `payments.status=failed`, 포인트 환불
3. 가상계좌 → `payments.status=virtual_account_issued`, `orders.status=pending`
4. 금액 위변조 (포트원 응답 ≠ 주문 금액) → 예외, payment 미생성
5. 동일 paymentId 재호출 → 기존 결과 반환
6. 타인 주문 결제 시도 → 403
7. 포인트 초과 사용 → 400

## 산출물
1. 패키지: `order/`, `payment/`, `common/` 하위 Controller/Service/Repository/Entity/DTO
2. `payments` 테이블 DDL
3. `application.yml` 예시
4. WebClient 설정 (`baseUrl=portone.api-base-url`)