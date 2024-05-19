package homeat.backend.domain.homeatreport.repository;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.homeatreport.entity.Week_Check;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeekCheckRepository extends JpaRepository<Week_Check, Long> {

    Optional<Week_Check> findTopByFinanceDataOrderByIdDesc(FinanceData financeData);
    Optional<Week_Check> findFirstByFinanceDataOrderByCreatedAtDesc(FinanceData financeData);
    Optional<Week_Check> findFirstByFinanceDataAndCreatedAtBetween(FinanceData financeData, LocalDateTime start, LocalDateTime end);
    List<Week_Check> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}
