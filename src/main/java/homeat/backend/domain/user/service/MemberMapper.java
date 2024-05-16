package homeat.backend.domain.user.service;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.address.entity.Address;
import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.dto.MemberResponse;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;

import java.time.LocalDateTime;

public class MemberMapper {

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

    public static MemberResponse.JoinResultDTO toJoinResultDTO(Member member, String token) {
        return MemberResponse.JoinResultDTO.builder()
                .token(token)
                .memberId(member.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static MemberResponse.LoginResultDTO toLoginResultDTO(String token, LocalDateTime expiredAt) {
        return MemberResponse.LoginResultDTO.builder()
                .token(token)
                .expiredAt(expiredAt)
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

    public static MemberResponse.EmailVerifyDto toEmailVerifyDTO(String authCode) {
        return MemberResponse.EmailVerifyDto.builder()
                .authCode(authCode)
                .build();
    }
}
