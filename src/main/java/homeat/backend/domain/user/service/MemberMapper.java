package homeat.backend.domain.user.service;

import homeat.backend.domain.user.dto.MemberResponse;
import homeat.backend.domain.user.entity.LoginType;
import homeat.backend.domain.user.entity.Member;

public class MemberMapper {
    public static Member toEmailMember(String email, String encodedPassword) {
        return Member.builder()
                .email(email)
                .password(encodedPassword)
                .build();
    }

    public static Member toKakaoMember(String kakaoId) {
        return Member.builder()
                .email(kakaoId)
                .loginType(LoginType.KAKAO)
                .build();
    }

    public static MemberResponse.refreshTokenDto toRefreshToken(String refreshToken) {
        return MemberResponse.refreshTokenDto.builder()
                .refreshToken(refreshToken)
                .build();
    }

    public static MemberResponse.emailCheckDto toEmailCheck(String authCode) {
        return MemberResponse.emailCheckDto.builder()
                .authCode(authCode)
                .build();
    }
}
