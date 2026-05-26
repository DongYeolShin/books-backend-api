package co.books.api.payment.repo;

import co.books.api.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 결제 리포지토리. PK 가 portone paymentId 이므로 ID 는 String 이다.
 */
@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {

    /** 주문 ID 로 결제 레코드를 조회한다. 한 주문에 결제 row 는 0~1개이므로 First 를 사용한다. */
    Optional<PaymentEntity> findFirstByOrderId(String orderId);
}
