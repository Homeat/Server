package homeat.backend.domain.post.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InfoTalkDTO {

    private String title;
    private String content;
    private List<String> tags;

}
