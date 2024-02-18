package homeat.backend.domain.homeatreport.repository;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.repository.querydsl.WeekRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeekRepository extends JpaRepository<Week, Long>, WeekRepositoryCustom {
    Optional<Week> findFirstByFinanceDataOrderByCreatedAtDesc(FinanceData financeData);
    Optional<Week> findFirstByFinanceDataAndCreatedAtBetween(FinanceData financeData, LocalDateTime start, LocalDateTime end);

    Optional<Week> findTopByFinanceDataOrderByIdDesc(FinanceData financeData);
}
