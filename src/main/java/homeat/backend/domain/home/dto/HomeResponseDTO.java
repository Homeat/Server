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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeResultDTO {
        private String nickname;
        private Long targetMoney;
        private int beforeSavingPercent;
        private Long remainingMoney;
        private String badgeImgUrl;
        private int remainingPercent;
        private int beforeWeek;
        private String message;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalendarResultDTO {
        private LocalDate date;
        private int todayJipbapPricePercent;
        private int todayOutPricePercent;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalendarDayResultDTO {
        private LocalDate date;
        private long todayJipbapPrice;
        private long todayOutPrice;
        private long remainingGoal;
        private String message;
    }
}