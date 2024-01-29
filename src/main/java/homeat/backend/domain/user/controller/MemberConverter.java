package homeat.backend.domain.user.controller;

import homeat.backend.domain.user.dto.MemberRequest;
import homeat.backend.domain.user.dto.MemberResponse;
import homeat.backend.domain.user.entity.Member;

import java.time.LocalDateTime;

public class MemberConverter {

    public static Member toMember(MemberRequest.JoinDto request) {
        return Member.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .build();
    }

    public static MemberResponse.JoinResultDTO toJoinResultDTO(Member member) {
        return MemberResponse.JoinResultDTO.builder()
                .memberId(member.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static MemberResponse.LoginResultDTO toLoginResultDTO(String token) {
        return MemberResponse.LoginResultDTO.builder()
                .token(token)
//                .expiredAt(LocalDateTime.now()+expriedTime)
                .build();
    }

    public static MemberResponse.MyPageResultDTO toMyPageResultDTO(Member member) {
        return MemberResponse.MyPageResultDTO.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
