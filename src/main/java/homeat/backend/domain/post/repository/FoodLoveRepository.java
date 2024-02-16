package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.FoodTalkLove;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodLoveRepository extends JpaRepository<FoodTalkLove, Long> {

    FoodTalkLove findByFoodTalkAndMember(FoodTalk foodTalk, Member member);
}
