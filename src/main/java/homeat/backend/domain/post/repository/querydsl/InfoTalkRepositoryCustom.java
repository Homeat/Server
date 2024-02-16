package homeat.backend.domain.post.repository.querydsl;

import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.InfoTalkSearchCondition;
import homeat.backend.domain.post.dto.queryDto.InfoTalkTotalView;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.InfoTalk;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface InfoTalkRepositoryCustom {

    InfoTalk findByInfoTalkId(Long id);

    Slice<InfoTalkTotalView> findByIdLessThanOrderByIdDesc(InfoTalkSearchCondition condition, Long lastInfoTalkId, Pageable pageable);

    Slice<InfoTalkTotalView> findByIdGreaterThanOrderByIdAsc(InfoTalkSearchCondition condition,Long oldestInfoTalkId, Pageable pageable);

    Slice<InfoTalkTotalView> findByLoveLessThanOrderByLoveDesc(InfoTalkSearchCondition condition,Long id, int love, Pageable pageable);

    Slice<InfoTalkTotalView> findByViewLessThanOrderByViewDesc(InfoTalkSearchCondition condition,Long id, int view, Pageable pageable);
}
