package homeat.backend.domain.post.dto;

import homeat.backend.domain.post.entity.Tag;
import java.io.Serializable;
import java.util.List;
import javax.mail.Multipart;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class FoodRequestDTO {

    @Getter
    public static class CommentDTO {
        Long id;
        String content;
    }

    @Data
    public static class FoodRecipeRequest {
        private List<FoodRecipeDTO> foodRecipeDTOS;
    }

    @Data
    public static class FoodRecipeDTO {
        private String recipe;
        private String ingredient;
        private MultipartFile recipePicture;


    }




}
