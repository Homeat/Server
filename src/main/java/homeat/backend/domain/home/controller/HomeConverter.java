package homeat.backend.domain.home.controller;

import homeat.backend.domain.home.dto.HomeResponseDTO;
import homeat.backend.domain.home.entity.DailyExpense;
import homeat.backend.domain.homeatreport.entity.Week;

public class HomeConverter {

    // request -> 엔티티
//    public static Week toDailyExpense(HomeRequestDTO.TargetExpenseDTO request) {
//        return Week.builder()
//                .goal_price(request.getTargetExpense())
//                .build();
//    }

    // 엔티티 -> response
//    public static HomeResponseDTO.ExpenseResultDTO toExpenseResult(Week week) {
//        return HomeResponseDTO.ExpenseResultDTO.builder()
//                .expense(week.getGoal_price())
//                .build();
//    }

    // 월별 데이터 entity -> dto
//    public static HomeResponseDTO.CalendarResultDTO toCalendarResult(DailyExpense expense) {
//        return HomeResponseDTO.CalendarResultDTO.builder()
//                .date(expense.getCreatedAt().toLocalDate())
//                .todayJipbapPricePercent(expense.getTodayJipbapPrice())
//                .todayOutPricePercent(expense.getTodayOutPrice())
//                .build();
//    }
}