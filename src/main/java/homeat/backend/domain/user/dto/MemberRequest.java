package homeat.backend.domain.user.dto;

import homeat.backend.domain.user.annotation.ExistEmail;
import lombok.Getter;

import javax.validation.constraints.*;

public class MemberRequest {
    @Getter
    public static class joinEmailDto {
        @NotBlank
        @Email
        @ExistEmail
        String email;

        @NotBlank
        String password;
    }

    @Getter
    public static class joinKakaoDto {
        @Min(0)
        Long kakaoId;
    }

    @Getter
    public static class loginDto {
        String email;
        String password;
    }

    @Getter
    public static class emailCheckDto {
        @NotBlank
        @Email
        String email;
    }

    @Getter
    public static class findPasswordDto {
        @NotBlank
        @Email
        String email;

        @NotBlank
        String newPassword;
    }
}
