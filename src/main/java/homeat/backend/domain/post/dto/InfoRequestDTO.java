package homeat.backend.domain.post.dto;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class InfoRequestDTO {

    @Getter
    public static class CommentDTO {
        @Min(value = 0, message = "ID 값은 최소 0입니다")
        Long id;
        @NotBlank(message = "댓글 내용이 비어 있습니다")
        String content;
    }
}
