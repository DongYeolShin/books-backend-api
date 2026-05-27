package co.books.api.user.dto;

/** 마이페이지 조회 응답. { code, data } 래퍼 형식. */
public record MyPageResponse(int code, MyPageData data) {

    public static MyPageResponse ok(MyPageData data) {
        return new MyPageResponse(200, data);
    }
}
