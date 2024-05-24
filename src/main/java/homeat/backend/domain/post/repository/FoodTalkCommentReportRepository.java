package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.FoodTalkComment;
import homeat.backend.domain.post.entity.FoodTalkCommentReport;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTalkCommentReportRepository extends JpaRepository<FoodTalkCommentReport, Long> {
    FoodTalkCommentReport findByFoodTalkCommentAndMember(FoodTalkComment foodTalkComment, Member member);
}
