package co.books.api.order.repo;

import co.books.api.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 주문 리포지토리.
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
}
