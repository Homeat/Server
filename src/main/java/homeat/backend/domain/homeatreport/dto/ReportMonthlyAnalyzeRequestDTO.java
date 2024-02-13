package homeat.backend.domain.homeatreport.dto;

import lombok.Getter;

public class ReportMonthlyAnalyzeRequestDTO {
    @Getter
    public static class DateInputDTO {

        private int input_year;

        private int input_month;
    }
}
