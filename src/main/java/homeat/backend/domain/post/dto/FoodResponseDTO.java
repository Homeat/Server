package homeat.backend.domain.post.dto;

import homeat.backend.domain.post.entity.Status;
import homeat.backend.domain.post.entity.Tag;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

public class FoodResponseDTO {

    @Getter
    @Builder
    public static class FoodTalkViewDTO {
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long id;
        private String profileImgUrl;
        private String postNickName;
        private String name;
        private String memo;
        private String ingredient;
        private Tag tag;
        private Integer love;
        private Integer view;
        private Integer commentNumber;
        private Boolean setLove;
        private Status status;
        private List<String> foodPictureImages;
        private List<FoodResponseDTO.FoodTalkRecipeViewDTO> foodTalkRecipes;
        private List<FoodResponseDTO.FoodTalkCommentViewDTO> foodTalkComments;
    }

    @Getter
    @Builder
    public static class FoodTalkRecipeViewDTO {
        private Integer step;
        private String recipe;
        private String ingredient;
        private String tip;
        private List<String> foodRecipeImages;
    }

    @Getter
    @Builder
    public static class FoodTalkCommentViewDTO {
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long commentId;
        private String commentNickName;
        private String content;
        private Status status;
        private List<FoodResponseDTO.FoodTalkReplyViewDTO> foodTalkReplies;
    }

    @Getter
    @Builder
    public static class FoodTalkReplyViewDTO {
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long replyId;
        private String replyNickName;
        private String content;
        private Status status;
    }








}
