package co.books.api.payment.dto;

import java.util.List;

/** 주문 완료 페이지 데이터 본문. */
public record OrderCompleteData(
        String orderId,
        List<OrderCompleteItem> orderList,
        int usedPoints,
        OrderCompleteOrderer orders,
        OrderCompleteShipping shipping
) {}
