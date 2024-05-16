package homeat.backend.domain.user.service;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.entity.Address;
import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.dto.MemberResponse;
import homeat.backend.domain.user.entity.LoginType;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;

import java.time.LocalDateTime;

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

    public static MemberResponse.emailCheckDto toEmailCheck(String authCode) {
        return MemberResponse.emailCheckDto.builder()
                .authCode(authCode)
                .build();
    }



    public static Member toMember(MemberRequest.JoinDto request) {
        return Member.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .build();
    }

    public static MemberInfo toMemberInfo(MemberRequest.CreateInfoDto request, Member member, Address address) {
        return MemberInfo.builder()
                .member(member)
                .address(address)
                .gender(request.getGender())
                .birth(request.getBirth())
                .income(request.getIncome())
                .build();
    }

    public static MemberResponse.MyPageResultDTO toMyPageResultDTO(Member member, MemberInfo memberInfo, AddressResponse.AddressDTO addressInfo) {
        return MemberResponse.MyPageResultDTO.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImgUrl(member.getProfileImgUrl())
                .gender(memberInfo.getGender())
                .birth(memberInfo.getBirth())
                .income(memberInfo.getIncome())
                .address(addressInfo)
                .build();
    }

    public static MemberResponse.CreateInfoResultDTO toCreateInfoResultDTO(MemberInfo memberInfo) {
        return MemberResponse.CreateInfoResultDTO.builder()
                .memberInfoId(memberInfo.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
