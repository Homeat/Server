package homeat.backend.domain.user.controller;

import homeat.backend.domain.user.dto.MemberResponse;
import homeat.backend.domain.user.entity.Member;

import java.time.LocalDateTime;

public class MemberConverter {

    public static MemberResponse.CreateResultDTO toCreateResultDTO(Member member) {
        return MemberResponse.CreateResultDTO.builder()
                .memberId(member.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
