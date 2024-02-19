package homeat.backend.domain.homeatreport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportMonthlyAnalyzeResponseDTO {

    private Long month_jipbap_price;
    private Long month_out_price;
    private int jipbap_ratio;
    private int out_ratio;
    private String save_percent;

}
