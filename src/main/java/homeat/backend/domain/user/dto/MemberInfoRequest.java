package homeat.backend.domain.user.dto;

import homeat.backend.domain.user.entity.Gender;
import lombok.Getter;

import java.time.LocalDate;

public class MemberInfoRequest {

    @Getter
    public static class CreateDto {
        Gender gender;
        LocalDate birth;
    }
}
