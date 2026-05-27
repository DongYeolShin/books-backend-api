package co.books.api.user.dto;

import java.util.List;

/** 마이페이지 응답 data 필드. */
public record MyPageData(
        MyInfoDto myInfo,
        List<RecentOrderDto> recentOrders
) {
}
