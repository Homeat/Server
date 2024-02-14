package homeat.backend.domain.homeatreport.dto;

import homeat.backend.domain.homeatreport.entity.WeekStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportBadgeResponseDTO {

    private Long week_id;
    private Long goal_price;
    private Long exceed_price;
    private WeekStatus weekStatus;
    private String badge_url;

}
