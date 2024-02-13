package homeat.backend.domain.homeatreport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportWeeklyResponseDTO {

    private String badge_img_url; // 뱃지 이미지 url
    private Long exceed_price; // 이번주 초과 금액
    private Long goal_price; // 이번주 목표 금액

}
