package co.books.api.user.dto;

/** 마이페이지 내 정보 DTO. */
public record MyInfoDto(
        String name,
        String birth,
        String gender,
        String phone,
        String email,
        String address,
        String addressDetail,
        Integer points
) {
}
