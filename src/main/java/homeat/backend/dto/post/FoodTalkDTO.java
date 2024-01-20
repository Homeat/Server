package homeat.backend.dto.post;

import homeat.backend.domain.post.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FoodTalkDTO {

    private String name;
    private String ingredient;
    private String memo;
    private Tag tag;
}
