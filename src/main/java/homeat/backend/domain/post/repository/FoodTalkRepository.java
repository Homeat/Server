package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.repository.querydsl.FoodTalkRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTalkRepository extends JpaRepository<FoodTalk, Long>, FoodTalkRepositoryCustom {


}
