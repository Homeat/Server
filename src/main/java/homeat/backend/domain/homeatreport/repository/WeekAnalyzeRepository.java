package homeat.backend.domain.homeatreport.repository;

import homeat.backend.domain.homeatreport.entity.Week_Analyze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeekAnalyzeRepository extends JpaRepository<Week_Analyze, Long> {
}
