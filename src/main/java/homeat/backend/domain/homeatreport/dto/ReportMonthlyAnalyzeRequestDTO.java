package homeat.backend.domain.homeatreport.dto;

import lombok.Getter;

public class ReportMonthlyAnalyzeRequestDTO {
    @Getter
    public static class DateInputDTO {

        private Integer input_year;

        private Integer input_month;

        private Integer input_day;
    }
}
