package homeat.backend.domain.post.dto;

import homeat.backend.domain.post.entity.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class InfoResponseDTO {

    @Getter
    @Builder
    public static class InfoTalkViewDTO {
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long id;
        private String postNickName;
        private String title;
        private String content;
        private List<String> tags;
        private Integer love;
        private Integer view;
        private Integer commentNumber;
        private Boolean setLove;
        private List<String> infoPictureImages;
        private List<InfoResponseDTO.InfoTalkCommentViewDTO> infoTalkComments;
    }

    @Getter
    @Builder
    public static class InfoTalkCommentViewDTO {
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long commentId;
        private String commentNickName;
        private String content;
        private List<InfoResponseDTO.InfoTalkReplyViewDTO> infoTalkReplies;
    }

    @Getter
    @Builder
    public static class InfoTalkReplyViewDTO {
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long replyId;
        private String replyNickName;
        private String content;
    }
}
