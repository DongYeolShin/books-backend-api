package co.books.api.order.repo;

import co.books.api.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 주문 상세 리포지토리.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    /** 주문 ID 로 해당 주문의 상품 목록을 조회한다. */
    List<OrderItemEntity> findByOrderId(String orderId);
}
