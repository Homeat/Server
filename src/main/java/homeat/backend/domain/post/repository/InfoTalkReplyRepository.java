package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.InfoTalkReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoTalkReplyRepository extends JpaRepository<InfoTalkReply, Long> {
}
