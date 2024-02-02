package homeat.backend.domain.home.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class HomeResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpenseResultDTO {
        private Long expense;
    }

//    public static class HomeResultDTO {
//
//    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalendarResultDTO {
        private LocalDate date;
        private Long todayJipbapPrice;
        private Long todayOutPrice;
    }
}