package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.FoodTalkComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodTalkCommentRepository extends JpaRepository<FoodTalkComment, Long> {
}
