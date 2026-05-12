package co.books.api.payment.service;

import co.books.api.common.exception.NotFoundException;
import co.books.api.common.exception.PortOneApiException;
import co.books.api.order.entity.OrderEntity;
import co.books.api.order.entity.OrderStatus;
import co.books.api.order.repo.OrderRepository;
import co.books.api.payment.dto.PaymentCompleteRequest;
import co.books.api.payment.dto.PaymentCompleteResponse;
import co.books.api.payment.dto.PortOnePaymentResponse;
import co.books.api.payment.entity.PayMethod;
import co.books.api.payment.entity.PaymentEntity;
import co.books.api.payment.entity.PaymentStatus;
import co.books.api.payment.repo.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
