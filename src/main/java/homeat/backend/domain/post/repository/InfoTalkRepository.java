package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.InfoTalk;
import homeat.backend.domain.post.repository.querydsl.InfoTalkRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface InfoTalkRepository extends JpaRepository<InfoTalk, Long>, InfoTalkRepositoryCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InfoTalk i WHERE i.id=:id")
    Optional<InfoTalk> findByIdForUpdate(Long id);

    @Modifying
    @Query("UPDATE InfoTalk i SET i.love = i.love + 1 WHERE i.id = :id")
    int updateLove(Long id);
}
