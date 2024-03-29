package homeat.backend.domain.post.dto;

import homeat.backend.domain.post.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FoodTalkDTO {

    private String name;
    private String memo;
    private Tag tag;
}
