package co.books.api.payment.dto;

/** 주문 완료 페이지 조회 응답. { code, data } 래퍼 형식. */
public record OrderCompleteResponse(int code, OrderCompleteData data) {

    public static OrderCompleteResponse ok(OrderCompleteData data) {
        return new OrderCompleteResponse(200, data);
    }
}
