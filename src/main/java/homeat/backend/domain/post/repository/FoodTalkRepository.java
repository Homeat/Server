package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.repository.querydsl.FoodTalkRepositoryCustom;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTalkRepository extends JpaRepository<FoodTalk, Long>, FoodTalkRepositoryCustom {

    List<FoodTalk> findByIdLessThanOrderByIdDesc(Long lastFoodTalkId, PageRequest pageRequest);

    List<FoodTalk> findByIdGreaterThanOrderByIdAsc(Long OldestFoodTalkId, PageRequest pageRequest);

    List<FoodTalk> findByLoveLessThanOrderByLoveDesc(int love);

    List<FoodTalk> findByViewLessThanOrderByViewDesc(int view);

}
