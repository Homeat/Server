package homeat.backend.domain.home.converter;

import homeat.backend.domain.home.entity.Management;
import homeat.backend.domain.home.dto.ManagementRequestDTO;
import homeat.backend.domain.home.dto.ManagementResponseDTO;

public class ManagementConverter {

    // request -> 엔티티
    public static Management toManagement(ManagementRequestDTO.ExpenseDTO request) {
        return Management.builder()
                .targetCost(request.getExpense())
                .build();
    }

    // 엔티티 -> response
    public static ManagementResponseDTO.ExpenseResultDTO toExpenseResult(Management management) {
        return ManagementResponseDTO.ExpenseResultDTO.builder()
                .expense(management.getTargetCost())
                .build();
    }
}