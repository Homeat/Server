package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.repository.querydsl.FoodTalkRepositoryCustom;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTalkRepository extends JpaRepository<FoodTalk, Long>, FoodTalkRepositoryCustom {



}
