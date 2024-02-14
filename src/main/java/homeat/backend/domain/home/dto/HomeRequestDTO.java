package homeat.backend.domain.home.dto;

import homeat.backend.domain.home.entity.CostType;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

public class HomeRequestDTO {

    // 목표 금액
    @Getter
    public static class nextTargetExpenseDTO {
        @NotBlank
        private Long nextTargetExpense;
    }

    // 영수증 등록
    @Getter
    public static class ReceiptDTO {
        private Long money;
        private CostType type;
        private String memo;
    }
}