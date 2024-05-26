package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.InfoTalkComment;
import homeat.backend.domain.post.entity.InfoTalkCommentReport;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoTalkCommentReportRepository extends JpaRepository<InfoTalkCommentReport, Long> {
    InfoTalkCommentReport findByInfoTalkCommentAndMember(InfoTalkComment infoTalkComment, Member member);
}
