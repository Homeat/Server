package homeat.backend.domain.post.dto.queryDto;

import homeat.backend.domain.post.entity.Tag;
import lombok.Data;

@Data
public class FoodTalkSearchCondition {

    private String search;
    private String tag;
}
