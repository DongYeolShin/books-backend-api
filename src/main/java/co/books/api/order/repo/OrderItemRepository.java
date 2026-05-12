package co.books.api.order.repo;

import co.books.api.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 주문 상세 리포지토리.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}
