package homeat.backend.domain.homeatreport.repository;

import homeat.backend.domain.homeatreport.entity.Week;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WeekRepository extends JpaRepository<Week, Long> {

}
