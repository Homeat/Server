package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.entity.InfoTalkReport;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoTalkReportRepository extends JpaRepository<InfoTalkReport, Long> {

    InfoTalkReport findByInfoTalkAndMember(InfoTalk infoTalk, Member member);
}
