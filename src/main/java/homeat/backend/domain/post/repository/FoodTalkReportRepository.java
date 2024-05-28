package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.FoodTalkReport;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTalkReportRepository extends JpaRepository<FoodTalkReport, Long> {

    FoodTalkReport findByFoodTalkAndMember(FoodTalk foodTalk, Member member);
}
