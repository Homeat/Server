package homeat.backend.repository.post;

import homeat.backend.domain.post.FoodTalkComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTalkCommentRepository extends JpaRepository<FoodTalkComment, Long> {
}
