package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.entity.InfoTalkLove;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoTalkLoveRepository extends JpaRepository<InfoTalkLove, Long> {

    InfoTalkLove findByInfoTalkAndMember(InfoTalk infoTalk, Member member);
}
