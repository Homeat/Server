package homeat.backend.domain.homeatreport.repository;

import homeat.backend.domain.homeatreport.entity.BadgePage_txt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgePageTxtRepository extends JpaRepository<BadgePage_txt, Long> {
}
