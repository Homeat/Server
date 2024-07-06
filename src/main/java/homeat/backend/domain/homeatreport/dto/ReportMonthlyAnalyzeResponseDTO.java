package homeat.backend.domain.homeatreport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportMonthlyAnalyzeResponseDTO {

    private Integer monthZeroExpense;
    private Long month_jipbap_price;
    private Long month_out_price;
    private Integer jipbap_ratio;
    private Integer out_ratio;
    private Double save_percent;

}
