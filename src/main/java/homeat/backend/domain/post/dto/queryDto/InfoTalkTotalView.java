package homeat.backend.domain.post.dto.queryDto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class InfoTalkTotalView {

    private Long infoTalkId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String title;
    private String content;
    private String url;
    private Integer love;
    private Integer commentNumber;

    public InfoTalkTotalView(Long infoTalkId, LocalDateTime createdAt, LocalDateTime updatedAt, String title,
                             String content, String url, Integer love, Integer commentNumber) {
        this.infoTalkId = infoTalkId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.title = title;
        this.content = content;
        this.url = url;
        this.love = love;
        this.commentNumber = commentNumber;
    }
}
