package co.books.api.payment.service;

import co.books.api.book.entity.BookEntity;
import co.books.api.book.repo.BookRepository;
import co.books.api.common.exception.NotFoundException;
import co.books.api.common.exception.PortOneApiException;
import co.books.api.order.entity.OrderEntity;
import co.books.api.order.entity.OrderItemEntity;
import co.books.api.order.entity.OrderStatus;
import co.books.api.order.repo.OrderItemRepository;
import co.books.api.order.repo.OrderRepository;
import co.books.api.payment.dto.OrderCompleteData;
import co.books.api.payment.dto.OrderCompleteItem;
import co.books.api.payment.dto.OrderCompleteOrderer;
import co.books.api.payment.dto.OrderCompleteResponse;
import co.books.api.payment.dto.OrderCompleteShipping;
import co.books.api.payment.dto.PaymentCancelRequest;
import co.books.api.payment.dto.PaymentCancelResponse;
import co.books.api.payment.dto.PaymentCompleteRequest;
import co.books.api.payment.dto.PaymentCompleteResponse;
import co.books.api.payment.dto.PortOneCancelResponse;
import co.books.api.payment.dto.PortOnePaymentResponse;
import co.books.api.payment.entity.PayMethod;
import co.books.api.payment.entity.PaymentEntity;
import co.books.api.payment.entity.PaymentStatus;
import co.books.api.payment.repo.PaymentRepository;
import co.books.api.user.entity.UserEntity;
import co.books.api.user.repo.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 결제 검증/완료 서비스.
 *
 * <p>웹훅을 사용하지 않고, 클라이언트가 결제 결과를 알리면 서버가 포트원 REST API 로
 * 직접 재조회하여 금액·상태를 검증한 뒤 payments INSERT 와 orders.status 갱신을 수행한다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PortOneApiClient portOneApiClient;
    private final ObjectMapper objectMapper;

    /**
     * 결제 검증 + 저장 + 주문상태 갱신을 트랜잭션으로 묶어 수행한다.
     *
     * @throws IllegalArgumentException paymentId/orderId 불일치 또는 입력 오류
     * @throws AccessDeniedException 본인 주문이 아닌 경우
     * @throws NotFoundException 주문이 존재하지 않는 경우
     * @throws IllegalStateException 결제 금액 위변조가 감지된 경우
     * @throws PortOneApiException 포트원 API 호출 실패
     */
    @Transactional
    public PaymentCompleteResponse complete(PaymentCompleteRequest req, String userId) {
        // 1. paymentId == orderId 검증
        if (!req.paymentId().equals(req.orderId())) {
            throw new IllegalArgumentException("paymentId 와 orderId 가 일치하지 않습니다.");
        }

        // 2. 멱등성 - 이미 처리된 결제면 기존 결과 그대로 반환
        Optional<PaymentEntity> exists = paymentRepository.findById(req.paymentId());
        if (exists.isPresent()) {
            log.info("기존 결제 결과 재사용: paymentId={}", req.paymentId());
            return PaymentCompleteResponse.from(exists.get());
        }

        // 3. 주문 조회 + 본인 확인
        OrderEntity order = orderRepository.findById(req.orderId())
                .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));
        if (!order.getUserId().equals(userId)) {
            throw new AccessDeniedException("본인의 주문이 아닙니다.");
        }

        // 4. 포트원 API 조회
        PortOnePaymentResponse pg = portOneApiClient.getPayment(req.paymentId());
        if (pg == null) {
            throw new PortOneApiException("포트원 응답이 비어 있습니다.");
        }

        // 5. 금액 검증 (★위변조 방지)
        Integer paidTotal = pg.getAmount() == null ? null : pg.getAmount().getTotal();
        if (paidTotal == null || !paidTotal.equals(order.getTotalAmount())) {
            throw new IllegalStateException(
                    "결제 금액이 주문 금액과 일치하지 않습니다. (주문=" + order.getTotalAmount()
                            + ", 결제=" + paidTotal + ")");
        }

        // 6. payments INSERT (raw_response 에 응답 원본 직렬화 저장)
        PaymentEntity payment = buildPayment(order, pg);
        paymentRepository.save(payment);

        // 7. orders.status 갱신
        //    스키마에 orders.used_points 컬럼이 없으므로 결제 실패 시 포인트 환불 로직은 적용하지 않는다.
        //    환불이 필요해지면 orders 에 used_points 컬럼을 추가하고 환불 분기를 다시 살린다.
        order.updateStatus(mapToOrderStatus(pg.getStatus()));

        log.info("결제 완료 처리: paymentId={}, status={}, amount={}",
                payment.getPaymentId(), payment.getStatus(), payment.getTotalAmount());
        return PaymentCompleteResponse.from(payment);
    }

    /**
     * 결제 취소 (전액 또는 부분).
     *
     * <p>본인 결제만 취소 가능하다.</p>
     * <p>전액 취소 시: payments.status = CANCELLED, orders.status = CANCELLED 로 갱신한다.</p>
     * <p>부분 취소 시: payments.status = PAID 유지, orders.status 는 그대로 유지한다.</p>
     * <p>PAID 가 아닌 상태에서 재요청하면 409 를 반환한다.</p>
     *
     * @param paymentId 취소할 결제 ID
     * @param req       취소 요청 본문 (cancelAmount, reason)
     * @param userId    현재 인증된 사용자 ID
     * @throws NotFoundException paymentId 에 해당하는 결제가 없는 경우
     * @throws AccessDeniedException 본인 결제가 아닌 경우
     * @throws IllegalArgumentException cancelAmount 가 결제 금액을 초과하는 경우
     * @throws IllegalStateException 취소 불가 상태인 경우 (PAID 아닌 모든 상태)
     * @throws PortOneApiException 포트원 API 호출 실패
     */
    @Transactional
    public PaymentCancelResponse cancel(String paymentId, PaymentCancelRequest req, String userId) {
        // 1. 결제 조회
        PaymentEntity payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("결제를 찾을 수 없습니다. paymentId=" + paymentId));

        // 2. 본인 확인
        if (!payment.getUserId().equals(userId)) {
            throw new AccessDeniedException("본인의 결제가 아닙니다.");
        }

        // 3. 결제 상태 확인: PAID 인 경우만 취소 허용. 나머지(FAILED/VIRTUAL_ACCOUNT_ISSUED/CANCELLED)는 409
        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException(
                    "취소할 수 없는 결제 상태입니다. 현재 상태: " + payment.getStatus().name());
        }

        // 4. 부분 취소 금액 유효성 검사
        if (req.cancelAmount() != null && req.cancelAmount() > payment.getTotalAmount()) {
            throw new IllegalArgumentException(
                    "취소 금액이 결제 금액을 초과합니다. (결제=" + payment.getTotalAmount()
                            + ", 요청=" + req.cancelAmount() + ")");
        }

        // 5. 주문 조회
        OrderEntity order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다. orderId=" + payment.getOrderId()));

        // 6. 포트원 취소 API 호출 (실패 시 예외 → 트랜잭션 롤백)
        boolean isFullCancel = (req.cancelAmount() == null);
        PortOneCancelResponse portOneRes = portOneApiClient.cancelPayment(paymentId, req.cancelAmount(), req.reason());

        // 7. payments/orders 상태 갱신
        int cancelledAmount = isFullCancel ? payment.getTotalAmount() : req.cancelAmount();
        Integer totalCancelled = extractTotalCancelled(portOneRes, cancelledAmount);

        if (isFullCancel) {
            // 전액 취소: payments.status = CANCELLED, orders.status = CANCELLED
            payment.setStatus(PaymentStatus.CANCELLED);
            order.updateStatus(OrderStatus.CANCELLED);
            log.info("전액 취소 완료: paymentId={}, userId={}", paymentId, userId);
        } else {
            // 부분 취소: payments.status = PAID 유지 (잔액 추적 컬럼 없음), orders.status 유지
            log.info("부분 취소 완료: paymentId={}, cancelAmount={}, userId={}", paymentId, cancelledAmount, userId);
        }

        String message = isFullCancel ? "전액 취소가 완료되었습니다." : "부분 취소가 완료되었습니다. 취소 금액: " + cancelledAmount + "원";

        return new PaymentCancelResponse(
                paymentId,
                payment.getStatus().name(),
                cancelledAmount,
                totalCancelled,
                message
        );
    }

    /**
     * 포트원 취소 응답에서 누적 취소 금액을 추출한다.
     * 응답이 없으면 이번 취소 금액을 그대로 반환한다.
     */
    private Integer extractTotalCancelled(PortOneCancelResponse res, int fallback) {
        if (res == null || res.getAmount() == null || res.getAmount().getCancelled() == null) {
            return fallback;
        }
        return res.getAmount().getCancelled();
    }

    /**
     * 포트원 응답으로부터 PaymentEntity 를 만든다.
     */
    private PaymentEntity buildPayment(OrderEntity order, PortOnePaymentResponse pg) {
        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentId(order.getOrderId());
        payment.setOrderId(order.getOrderId());
        payment.setUserId(order.getUserId());
        payment.setTxId(pg.getTransactionId());
        payment.setChannelKey(pg.getChannel() == null ? "unknown" : pg.getChannel().getKey());
        payment.setPgProvider(pg.getChannel() == null ? null : pg.getChannel().getPgProvider());
        payment.setStatus(PaymentStatus.fromPortOne(pg.getStatus()));
        payment.setPayMethod(pg.getMethod() == null ? null : PayMethod.fromPortOneMethodType(pg.getMethod().getType()));
        payment.setTotalAmount(pg.getAmount() == null ? order.getTotalAmount() : pg.getAmount().getTotal());
        payment.setCurrency(pg.getAmount() == null || pg.getAmount().getCurrency() == null
                ? "KRW" : pg.getAmount().getCurrency());
        payment.setOrderName(pg.getOrderName() == null ? order.getOrderId() : pg.getOrderName());
        if (pg.getFailure() != null) {
            payment.setFailReason(pg.getFailure().getReason());
            payment.setFailCode(pg.getFailure().getPgCode());
        }
        payment.setPaidAt(pg.getPaidAt());
        payment.setRawResponse(toJson(pg));
        return payment;
    }

    private String toJson(PortOnePaymentResponse pg) {
        try {
            return objectMapper.writeValueAsString(pg);
        } catch (JsonProcessingException e) {
            log.warn("포트원 응답 직렬화 실패: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * 주문 완료 페이지에 필요한 주문·결제·배송·주문자 정보를 조합하여 반환한다.
     *
     * @param orderId 조회할 주문 ID
     * @param userId  현재 인증된 사용자 ID
     * @throws NotFoundException 주문이 존재하지 않는 경우
     * @throws AccessDeniedException 본인 주문이 아닌 경우
     */
    @Transactional(readOnly = true)
    public OrderCompleteResponse getOrderComplete(String orderId, String userId) {
        // 1. 주문 조회
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("주문을 찾을 수 없습니다."));

        // 2. 본인 확인
        if (!order.getUserId().equals(userId)) {
            throw new AccessDeniedException("본인의 주문이 아닙니다.");
        }

        // 3. 주문 상품 목록 조회
        List<OrderItemEntity> items = orderItemRepository.findByOrderId(orderId);

        // 4. 도서 일괄 조회 → Map<bookId, BookEntity>
        List<String> bookIds = items.stream().map(OrderItemEntity::getBookId).toList();
        Map<String, BookEntity> bookMap = bookRepository.findAllById(bookIds)
                .stream().collect(Collectors.toMap(BookEntity::getBookId, b -> b));

        // 5. 주문자 정보 조회 (email 포함)
        UserEntity user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        // 6. 결제 정보 조회 (없을 수 있음)
        Optional<PaymentEntity> payment = paymentRepository.findFirstByOrderId(orderId);

        // 7. 주문일 Asia/Seoul 기준 yyyy-MM-dd 변환
        String orderDate = order.getOrderedAt()
                .atZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDate()
                .toString();

        // 8. 주문 상태 한글 라벨 변환
        String statusLabel = switch (order.getStatus()) {
            case PENDING -> "주문 대기";
            case PAID -> "결제 완료";
            case SHIPPED -> "배송중";
            case DELIVERED -> "배송 완료";
            case CANCELLED -> "취소됨";
            case FAILED -> "결제 실패";
        };

        // 9. 상품 목록 구성
        List<OrderCompleteItem> orderList = items.stream()
                .map(item -> {
                    BookEntity book = bookMap.get(item.getBookId());
                    return new OrderCompleteItem(
                            item.getBookId(),
                            book != null ? book.getTitle() : "",
                            book != null ? book.getOriginalPrice() : 0,
                            item.getPriceAtPurchase(),
                            item.getQuantity()
                    );
                })
                .toList();

        // 10. 응답 조합
        OrderCompleteOrderer orderer = new OrderCompleteOrderer(
                user.getName(),
                order.getPhone(),
                user.getEmail(),
                payment.map(PaymentEntity::getPgProvider).orElse(null),
                orderDate
        );

        OrderCompleteShipping shipping = new OrderCompleteShipping(
                order.getReceiver(),
                order.getPhone(),
                order.getShippingAddress(),
                order.getShippingDetailAddress(),
                statusLabel
        );

        return OrderCompleteResponse.ok(new OrderCompleteData(
                orderId,
                orderList,
                order.getUsedPoints(),
                orderer,
                shipping
        ));
    }

    /**
     * 포트원 status → orders.status 매핑.
     */
    private OrderStatus mapToOrderStatus(String portOneStatus) {
        if (portOneStatus == null) return OrderStatus.PENDING;
        return switch (portOneStatus.toUpperCase()) {
            case "PAID" -> OrderStatus.PAID;
            case "FAILED" -> OrderStatus.FAILED;
            case "VIRTUAL_ACCOUNT_ISSUED" -> OrderStatus.PENDING;
            default -> OrderStatus.PENDING;
        };
    }

}
