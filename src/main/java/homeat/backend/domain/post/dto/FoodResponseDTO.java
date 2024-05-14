package homeat.backend.domain.post.dto;

import homeat.backend.domain.post.entity.Tag;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class FoodResponseDTO {

    @Getter
    @Builder
    public static class FoodTalkSaveDTO {

        private Long id;
        private String nickname;
        private String name;
        private String memo;
        private Tag tag;
    }

}
