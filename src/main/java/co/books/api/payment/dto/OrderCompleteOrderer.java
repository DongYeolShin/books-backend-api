package co.books.api.payment.dto;

/** 주문자 정보. */
public record OrderCompleteOrderer(
        String name,
        String phone,
        String email,
        String paymethod,
        String orderDate
) {}
