package co.books.api.order.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * {@link OrderStatus} (Java) ↔ order_status (PostgreSQL ENUM) 변환기.
 *
 * <p>JDBC URL 에 {@code stringtype=unspecified} 가 설정되어 있어,
 * VARCHAR 로 전송되는 값을 PG 가 자동으로 order_status ENUM 으로 캐스트한다.</p>
 */
@Converter
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(OrderStatus attribute) {
        return attribute == null ? null : attribute.toDbValue();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String dbData) {
        return OrderStatus.fromDbValue(dbData);
    }
}
