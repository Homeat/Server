package homeat.backend.domain.user.dto;

import homeat.backend.domain.user.annotation.ExistEmail;
import homeat.backend.domain.user.annotation.ExistNickname;
import homeat.backend.domain.user.annotation.ValidEnum;
import homeat.backend.domain.user.entity.Gender;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
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
}
