package homeat.backend.domain.home.converter;

import homeat.backend.domain.home.dto.ManagementRequestDTO;
import homeat.backend.domain.home.dto.ManagementResponseDTO;
import homeat.backend.domain.home.entity.DailyExpense;

public class ManagementConverter {

    // request -> 엔티티
    public static DailyExpense toManagement(ManagementRequestDTO.ExpenseDTO request) {
        return DailyExpense.builder()
                .targetCost(request.getExpense())
                .build();
    }

    // 엔티티 -> response
    public static ManagementResponseDTO.ExpenseResultDTO toExpenseResult(DailyExpense dailyExpense) {
        return ManagementResponseDTO.ExpenseResultDTO.builder()
                .expense(dailyExpense.getTargetCost())
                .build();
    }
}