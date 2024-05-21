package homeat.backend.domain.homeatreport.repository;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.homeatreport.entity.Week_Analyze;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeekAnalyzeRepository extends JpaRepository<Week_Analyze, Long> {
    List<Week_Analyze> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
