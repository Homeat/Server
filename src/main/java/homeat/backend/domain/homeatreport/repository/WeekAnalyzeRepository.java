package homeat.backend.domain.homeatreport.repository;

import homeat.backend.domain.homeatreport.entity.WeekAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WeekAnalyzeRepository extends JpaRepository<WeekAnalyze, Long> {
    List<WeekAnalyze> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
