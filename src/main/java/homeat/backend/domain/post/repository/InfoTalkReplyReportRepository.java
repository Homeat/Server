package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.InfoTalkReply;
import homeat.backend.domain.post.entity.InfoTalkReplyReport;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoTalkReplyReportRepository extends JpaRepository<InfoTalkReplyReport, Long> {

    InfoTalkReplyReport findByInfoTalkReplyAndMember(InfoTalkReply infoTalkReply, Member member);
}
