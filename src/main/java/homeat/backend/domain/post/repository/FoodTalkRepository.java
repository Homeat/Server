package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.dto.queryDto.FoodTalkSearchCondition;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.repository.querydsl.FoodTalkRepositoryCustom;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;

public interface FoodTalkRepository extends JpaRepository<FoodTalk, Long>, FoodTalkRepositoryCustom {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM FoodTalk f WHERE f.id=:id")
    Optional<FoodTalk> findByIdForUpdate(Long id);

    @Modifying
    @Query("UPDATE FoodTalk f SET f.love = f.love + 1 WHERE f.id = :id")
    int updateLove(Long id);

}
