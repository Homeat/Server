package homeat.backend.repository.post;

import homeat.backend.domain.post.InfoTalkComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoTalkCommentRepository extends JpaRepository<InfoTalkComment, Long> {
}
