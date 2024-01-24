package homeat.backend.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

public class MemberRequest {

    @Getter
    public static class JoinDto {
        String email;
        @Setter
        String password;
        String nickname;
    }
}
