package homeat.backend.domain.user.dto;

import lombok.Getter;

public class MemberRequest {

    @Getter
    public static class JoinDto {
        String email;
        String password;
        String nickname;
    }
}
