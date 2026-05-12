package co.books.api.payment.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PayMethodConverter implements AttributeConverter<PayMethod, String> {

    @Override
    public String convertToDatabaseColumn(PayMethod attribute) {
        return attribute == null ? null : attribute.toDbValue();
    }

    @Override
    public PayMethod convertToEntityAttribute(String dbData) {
        return PayMethod.fromDbValue(dbData);
    }
}
