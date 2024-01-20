package homeat.backend.repository.post;

import homeat.backend.domain.post.InfoHashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoHashTagRepository extends JpaRepository<InfoHashTag, Long> {
}
