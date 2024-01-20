package homeat.backend.repository.post;

import homeat.backend.domain.post.InfoTalk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoTalkRepository extends JpaRepository<InfoTalk, Long> {
}
