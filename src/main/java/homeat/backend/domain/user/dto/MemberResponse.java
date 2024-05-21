package homeat.backend.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberResponse {
    @Getter
    @Builder
    public static class emailCheckDto {
        String authCode;
    }
}
