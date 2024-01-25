package homeat.backend.domain.user.dto;

import homeat.backend.domain.user.annotation.ExistEmail;
import homeat.backend.domain.user.annotation.ExistNickname;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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
}
