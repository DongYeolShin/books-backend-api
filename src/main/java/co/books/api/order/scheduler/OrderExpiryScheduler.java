package co.books.api.order.scheduler;

import co.books.api.order.entity.OrderEntity;
import co.books.api.order.repo.OrderRepository;
import co.books.api.user.entity.UserEntity;
import co.books.api.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * PENDING 상태로 10분 이상 경과한 주문을 삭제하는 만료 스케줄러.
 *
 * <p>5분 간격(cron) 으로 실행되며, 삭제 전 사용된 포인트를 복원하고
 * orders 행 삭제 시 ON DELETE CASCADE 로 order_items 가 함께 삭제된다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExpiryScheduler {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    /**
     * 만료 주문 삭제 메서드. 매 5분(정각 기준) 에 실행된다.
     *
     * <p>포인트 복원과 deleteAll 이 단일 트랜잭션으로 묶여 원자적으로 커밋된다.
     * 예외 발생 시 트랜잭션 롤백 후 로그를 남기고 다음 주기에 재시도한다.</p>
     */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void expireOrders() {
        try {
            OffsetDateTime threshold = OffsetDateTime.now().minusMinutes(10);
            List<OrderEntity> expired = orderRepository.findExpiredPendingOrders(threshold);

            if (expired.isEmpty()) {
                return;
            }

            // 포인트 복원: usedPoints > 0 인 주문에 대해서만 UserEntity 더티 체킹으로 UPDATE
            for (OrderEntity order : expired) {
                if (order.getUsedPoints() > 0) {
                    UserEntity user = userRepository.findById(order.getUserId()).orElse(null);
                    if (user != null) {
                        user.setPoints(user.getPoints() + order.getUsedPoints());
                    }
                }
            }

            // orders 삭제 (ON DELETE CASCADE 로 order_items 자동 삭제)
            orderRepository.deleteAll(expired);

            log.info("만료 주문 삭제: {}건", expired.size());
        } catch (Exception e) {
            log.error("만료 주문 삭제 중 오류 발생", e);
        }
    }
}
