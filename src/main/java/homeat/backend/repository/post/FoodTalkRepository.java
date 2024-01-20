package homeat.backend.repository.post;

import homeat.backend.domain.post.FoodTalk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodTalkRepository extends JpaRepository<FoodTalk, Long> {
}
