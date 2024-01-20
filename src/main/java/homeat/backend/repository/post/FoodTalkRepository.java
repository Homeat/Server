package homeat.backend.repository.post;

import homeat.backend.domain.post.FoodTalk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTalkRepository extends JpaRepository<FoodTalk, Long> {
}
