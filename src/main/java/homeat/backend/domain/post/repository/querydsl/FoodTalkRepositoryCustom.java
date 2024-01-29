package homeat.backend.domain.post.repository.querydsl;

import homeat.backend.domain.post.dto.queryDto.FoodTalkResponse;
import homeat.backend.domain.post.entity.FoodTalk;
import java.util.List;

public interface FoodTalkRepositoryCustom {

    FoodTalk findByFoodTalkId(Long id);
}
