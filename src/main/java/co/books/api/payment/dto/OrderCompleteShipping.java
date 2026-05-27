package co.books.api.payment.dto;

/** 배송지 정보. */
public record OrderCompleteShipping(
        String name,
        String phone,
        String address,
        String addressDetail,
        String status
) {}
