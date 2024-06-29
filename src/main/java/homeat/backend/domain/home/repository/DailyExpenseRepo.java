package homeat.backend.domain.home.repository;

import homeat.backend.domain.home.entity.DailyExpense;
import homeat.backend.domain.home.repository.querydsl.DailyExpenseRepoCST;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyExpenseRepo extends JpaRepository<DailyExpense, Long>, DailyExpenseRepoCST {
    List<DailyExpense> findByFinanceDataIdOrderByDate(Long financeDataId);

    List<DailyExpense> findDailyExpenseByFinanceDataIdAndDateBetween(Long financeDataId, LocalDate startDate, LocalDate endDate);

    Optional<DailyExpense> findDailyExpenseByFinanceDataIdAndDate(Long financeDataId, LocalDate date);
}