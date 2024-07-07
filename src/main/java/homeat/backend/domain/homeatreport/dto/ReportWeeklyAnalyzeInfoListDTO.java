package homeat.backend.domain.homeatreport.dto;

import homeat.backend.domain.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportWeeklyAnalyzeInfoListDTO {

    String nickname;
    Integer ageIndex;
    String ageRange;
    Gender gender;
    Long income;

}
