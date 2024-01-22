package homeat.backend.domain.user.dto;

import lombok.Getter;

public class MemberRequest {

    @Getter
    public static class CreateDto {
        String email;
        String password;
        String nickname;
    }
}
