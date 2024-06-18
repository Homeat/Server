package homeat.backend.domain.homeatreport.repository;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.homeatreport.entity.WeekCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeekCheckRepository extends JpaRepository<WeekCheck, Long> {

    Optional<WeekCheck> findTopByFinanceDataOrderByIdDesc(FinanceData financeData);
    Optional<WeekCheck> findFirstByFinanceDataOrderByCreatedAtDesc(FinanceData financeData);
    Optional<WeekCheck> findFirstByFinanceDataAndCreatedAtBetween(FinanceData financeData, LocalDateTime start, LocalDateTime end);
    List<WeekCheck> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}
