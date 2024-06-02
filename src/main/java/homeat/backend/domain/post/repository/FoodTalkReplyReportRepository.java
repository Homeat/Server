package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.FoodTalkReply;
import homeat.backend.domain.post.entity.FoodTalkReplyReport;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTalkReplyReportRepository extends JpaRepository<FoodTalkReplyReport, Long> {

    FoodTalkReplyReport findByFoodTalkReplyAndMember(FoodTalkReply foodTalkReply, Member member);
}
