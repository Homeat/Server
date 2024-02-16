package homeat.backend.domain.post.repository.querydsl;

import com.querydsl.core.Tuple;
import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.FoodTalkTotalView;
import homeat.backend.domain.post.entity.FoodTalk;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FoodTalkRepositoryCustom {

    FoodTalk findByFoodTalkId(Long id);

    Slice<FoodTalkTotalView> findByIdLessThanOrderByIdDesc(FoodTalkSearchCondition condition, Long lastFoodTalkId, Pageable pageable);

    Slice<FoodTalkTotalView> findByIdGreaterThanOrderByIdAsc(FoodTalkSearchCondition condition,Long OldestFoodTalkId, Pageable pageable);

    Slice<FoodTalkTotalView> findByLoveLessThanOrderByLoveDesc(FoodTalkSearchCondition condition,Long id, int love, Pageable pageable);

    Slice<FoodTalkTotalView> findByViewLessThanOrderByViewDesc(FoodTalkSearchCondition condition,Long id, int view, Pageable pageable);

    Long countTotalCommentNumber(Long foodTalkId);

    Long countTotalReplyNumber(Long foodTalkCommentId);
}
