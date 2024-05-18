/*
package homeat.backend.domain.homeatreport.repository;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.repository.querydsl.WeekRepositoryCustom;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface WeekRepository extends JpaRepository<Week, Long>, WeekRepositoryCustom {
    //Optional<Week> findFirstByFinanceDataOrderByCreatedAtDesc(FinanceData financeData);
    //Optional<Week> findFirstByFinanceDataAndCreatedAtBetween(FinanceData financeData, LocalDateTime start, LocalDateTime end);

    Optional<Week> findTopByFinanceDataOrderByIdDesc(FinanceData financeData);

    Optional<Week> findTopByFinanceDataOrderByFinanceDataIdDesc(FinanceData financeData);
    //List<Week> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
*/
