package homeat.backend.domain.post.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class InfoRequestDTO {

    @Getter
    @Builder
    public static class InfoTalkDTO {

        private String title;
        private String content;
        private List<String> tags;

    }

    @Getter
    public static class CommentDTO {
        Long id;
        String content;
    }
}
