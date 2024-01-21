package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.InfoHashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoHashTagRepository extends JpaRepository<InfoHashTag, Long> {
}
