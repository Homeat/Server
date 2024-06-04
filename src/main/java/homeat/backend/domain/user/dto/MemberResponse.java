package homeat.backend.domain.user.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

public class MemberResponse {
    @Getter
    @Builder
    public static class emailCheckDto {
        String authCode;
    }

    @Getter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class joinKakaoDto {
        Long id;
        KakaoAccount kakaoAccount;
    }

    @Getter
    public static class KakaoAccount {
        Profile profile;
    }

    @Getter
    public static class Profile {
        String nickname;
    }
}
