package homeat.backend.domain.mypage.dto;

import homeat.backend.domain.user.annotation.ExistEmail;
import homeat.backend.domain.user.annotation.ExistNickname;
import homeat.backend.domain.user.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

public class MyPageRequest {
    @Getter
    public static class postInfoDto {
        @NotBlank
        @ExistNickname
        String nickname;

        @Schema(example = "MALE, FEMALE (둘 중 하나, 대문자 필수)")
        Gender gender;

        @Past
        LocalDate birth;

        @Min(value = 1)
        Long adderessId;

        @Min(value = 0)
        Long income;

        @Min(value = 0)
        Long goalPrice;
    }

    @Getter
    public static class updateNicknameDto {
        @NotBlank
        String nickname;
    }

    @Getter
    public static class updateIncomeDto {
        @Min(value = 0)
        Long income;
    }

    @Getter
    public static class postExistNicknameDto {
        @NotBlank
        String nickname;
    }

    @Getter
    public static class patchPasswordDto {
        @NotBlank
        String originPassword;

        @NotBlank
        String newPassword;
    }
}
