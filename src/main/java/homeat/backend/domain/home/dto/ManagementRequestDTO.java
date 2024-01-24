package homeat.backend.domain.home.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

public class ManagementRequestDTO {

    @Getter
    public static class ExpenseDTO {
        @NotBlank
        private Long expense;
    }
}