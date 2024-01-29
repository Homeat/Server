package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.repository.querydsl.InfoTalkRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoTalkRepository extends JpaRepository<InfoTalk, Long>, InfoTalkRepositoryCustom {
}
