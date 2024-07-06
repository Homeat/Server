package homeat.backend.domain.homeatreport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportWeeklyAnalyzeInfoResponseDTO {

    private String nickname;
    private String gender;
    private String income;
    private String ageRange;
}
