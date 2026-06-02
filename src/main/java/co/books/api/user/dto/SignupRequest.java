package co.books.api.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청 본문.
 * userId, email, passwd, name, phone, birthDate, postalCode, address 는 필수이며,
 * gender, addressDetail 은 선택 입력이다.
 */
public record SignupRequest(

        /** 회원 ID (로그인 식별자, 사용자가 직접 지정) */
        @NotBlank(message = "아이디는 필수입니다.")
        @Size(max = 100, message = "아이디는 100자 이하여야 합니다.")
        String userId,

        /** 이메일 주소 */
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
        String email,

        /** 비밀번호 (저장 전 BCrypt 인코딩) */
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String passwd,

        /** 회원 이름 */
        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 50, message = "이름은 50자 이하여야 합니다.")
        String name,

        /** 전화번호 (필수) */
        @NotBlank(message = "전화번호는 필수입니다.")
        @Size(max = 20, message = "전화번호는 20자 이하여야 합니다.")
        String phone,

        /** 성별 (선택, 예: 남자/여자) */
        String gender,

        /** 생년월일 (필수, yyyy-MM-dd 포맷) */
        @NotBlank(message = "생년월일은 필수입니다.")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일은 yyyy-MM-dd 형식이어야 합니다.")
        String birthDate,

        /** 우편번호 (필수) */
        @NotBlank(message = "우편번호는 필수입니다.")
        @Size(max = 10, message = "우편번호는 10자 이하여야 합니다.")
        String postalCode,

        /** 기본 주소 (필수) */
        @NotBlank(message = "주소는 필수입니다.")
        @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
        String address,

        /** 상세 주소 (선택) */
        String addressDetail
) {
}
