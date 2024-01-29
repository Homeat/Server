package homeat.backend.domain.home.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

public class HomeRequestDTO {

    // 목표 금액
    @Getter
    public static class TargetExpenseDTO {
        @NotBlank
        private Long targetExpense;
    }

    // 영수증 사진
    public static class ReceiptDTO {
        private MultipartFile imageFile;
    }
}