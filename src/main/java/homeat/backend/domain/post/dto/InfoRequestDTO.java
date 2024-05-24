package homeat.backend.domain.post.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class InfoRequestDTO {

    @Getter
    public static class CommentDTO {
        Long id;
        String content;
    }
}
