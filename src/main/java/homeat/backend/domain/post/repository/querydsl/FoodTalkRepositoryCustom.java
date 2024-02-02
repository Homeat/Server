package homeat.backend.domain.post.repository.querydsl;

import homeat.backend.domain.post.entity.FoodTalk;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FoodTalkRepositoryCustom {

    FoodTalk findByFoodTalkId(Long id);

    Slice<FoodTalk> findByLoveLessThanOrderByLoveDesc(Long id, int love, Pageable pageable);

    Slice<FoodTalk> findByViewLessThanOrderByViewDesc(Long id, int view, Pageable pageable);
}
