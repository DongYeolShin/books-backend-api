package co.books.api.payment.repo;

import co.books.api.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 결제 리포지토리. PK 가 portone paymentId 이므로 ID 는 String 이다.
 */
@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
}
