package homeat.backend.domain.mypage.dto;

import homeat.backend.domain.address.dto.AddressResponse;
import homeat.backend.domain.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class MyPageResponse {
    @Getter
    @Builder
    public static class getInfoDto {
        String nickname;
        String profileImgUrl;
    }

    @Getter
    @Builder
    public static class getDetailInfoDto {
        String email;
        String nickname;
        String profileImgUrl;
        Gender gender;
        LocalDate birth;
        Long income;
        AddressResponse.AddressDTO address;
    }
}
