package homeat.backend.domain.post.dto;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class FoodRequestDTO {

    @Getter
    public static class CommentDTO {
        @Min(value = 0, message = "ID 값은 최소 0입니다")
        Long id;
        @NotBlank(message = "댓글 내용이 비어 있습니다")
        String content;
    }

    @Data
    public static class FoodRecipeRequest {
        private List<FoodRecipeDTO> foodRecipeDTOS;
    }

    @Data
    public static class FoodRecipeDTO {
        @NotBlank(message = "recipe 가 비어있습니다")
        private String recipe;
        @NotNull(message = "레시피 사진이 존재하지않습니다")
        private MultipartFile recipePicture;


    }




}
