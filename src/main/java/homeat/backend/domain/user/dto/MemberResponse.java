package homeat.backend.domain.user.dto;

import homeat.backend.domain.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MemberResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinResultDTO {
        String token;
        Long memberId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResultDTO {
        String token;
        LocalDateTime expiredAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyPageResultDTO {
        String email;
        String nickname;
        String profileImgUrl;
        Gender gender;
        LocalDate birth;
        Long income;
        AddressResponse.NeighborhoodResultDTO address;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateInfoResultDTO {
        Long memberInfoId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailVerifyDto {
        String authCode;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetNeighborhoodResultDTO {
        Long totalColumnCount;
        Long totlaPageNum;
        List<AddressResponse.NeighborhoodResultDTO> neighborhoods;
    }
}
