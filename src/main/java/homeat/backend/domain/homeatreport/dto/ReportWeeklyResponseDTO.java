package homeat.backend.domain.homeatreport.dto;

import homeat.backend.domain.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportWeeklyResponseDTO {

    private String age_range; // 연령대
    private String income; // 수입"(소득 **만원 이하)
    private String gender; // 성별
    private String nickname; // 닉네임
    private Long jipbap_save; // 집밥 절약 비용
    private Long out_save; // 외식/배달 절약 비용
    private Long jipbap_average; // 비교군 집밥 평균 지출 금액
    private Long week_jipbap_price; // 본인 집밥 지출 금액
    private Long out_average;
    private Long week_out_price;

}
