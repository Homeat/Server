package homeat.backend.domain.post.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InfoTalkDTO {

    private String title;
    private String content;
    private List<String> tags;

}
