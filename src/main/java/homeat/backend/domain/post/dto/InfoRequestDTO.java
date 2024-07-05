package homeat.backend.domain.post.dto;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class InfoRequestDTO {

    @Getter
    public static class CommentDTO {
        @NotBlank(message = "댓글 내용이 비어 있습니다")
        String content;
    }
}
