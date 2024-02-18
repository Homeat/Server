package homeat.backend.domain.home.dto;

import homeat.backend.domain.home.entity.CostType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
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
        @Min(value = 0)
        private Long money;

        @Schema(example = "장보기 / 외식비 / 배달비")
        private CostType type;

        @NotBlank
        private String memo;
    }
}