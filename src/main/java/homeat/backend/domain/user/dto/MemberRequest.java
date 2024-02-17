package homeat.backend.domain.user.dto;

import homeat.backend.domain.user.annotation.ExistEmail;
import homeat.backend.domain.user.annotation.ExistNickname;
import homeat.backend.domain.user.annotation.ValidEnum;
import homeat.backend.domain.user.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class MemberRequest {

    @Getter
    @Setter
    public static class JoinDto {
        @NotBlank
        @Email
        @ExistEmail
        String email;

        @NotBlank
        String password;

        @NotBlank
        @ExistNickname
        String nickname;
    }

    @Getter
    public static class LoginDto {
        @NotBlank
        @Email
        String email;

        @NotBlank
        String password;
    }

    @Getter
    public static class CreateInfoDto {
        @Schema(example = "MALE, FEMALE (둘 중 하나, 대문자 필수)")
        Gender gender;

        @Past
        LocalDate birth;

        @Min(value = 0)
        Long income;

        @Min(value = 0)
        Long goalPrice;
    }

    @Getter
    public static class EmailVerifyDto {
        @NotBlank
        @Email
        @ExistEmail
        String email;
    }

    @Getter
    public static class UpdatePasswordDto {
        @NotBlank
        String originPassword;

        @NotBlank
        String newPassword;
    }

    @Getter
    public static class UpdateInfoDto {
        @Nullable
        @Email
        @ExistEmail
        @Schema(example = "null 가능, 필요한 항목만 넣으면 됨, 아래 income도 동일")
        String email;

        @Nullable
        @ExistNickname
        @Schema(example = "null 가능, 필요한 항목만 넣으면 됨, 아래 income도 동일")
        String nickname;

        @Nullable
        @Min(value = 0)
        Long income;
    }
}
