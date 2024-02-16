package homeat.backend.domain.homeatreport.repository;

import homeat.backend.domain.homeatreport.entity.Badge_img;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BadgeImgRepository extends JpaRepository<Badge_img, Long> {

    Optional<Badge_img> findBadge_imgById(Long Badge_img_id);
}
