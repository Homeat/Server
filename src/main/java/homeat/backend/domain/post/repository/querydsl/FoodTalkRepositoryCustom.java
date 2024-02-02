package homeat.backend.domain.post.repository.querydsl;

import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.entity.FoodTalk;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FoodTalkRepositoryCustom {

    FoodTalk findByFoodTalkId(Long id);

    Slice<FoodTalk> findByIdLessThanOrderByIdDesc(FoodTalkSearchCondition condition,Long lastFoodTalkId, Pageable pageable);

    Slice<FoodTalk> findByIdGreaterThanOrderByIdAsc(FoodTalkSearchCondition condition,Long OldestFoodTalkId, Pageable pageable);

    Slice<FoodTalk> findByLoveLessThanOrderByLoveDesc(FoodTalkSearchCondition condition,Long id, int love, Pageable pageable);

    Slice<FoodTalk> findByViewLessThanOrderByViewDesc(FoodTalkSearchCondition condition,Long id, int view, Pageable pageable);
}
