package homeat.backend.domain.post.repository.querydsl;

import homeat.backend.domain.post.entity.FoodTalk;

public interface FoodTalkRepositoryCustom {

    FoodTalk findByFoodTalkId(Long id);
}
