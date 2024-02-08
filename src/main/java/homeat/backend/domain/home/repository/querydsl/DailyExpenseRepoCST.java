package homeat.backend.domain.home.repository.querydsl;

import homeat.backend.domain.home.entity.DailyExpense;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyExpenseRepoCST {
    Long sumPricesBetweenDates(LocalDate startDate, LocalDate endDate);
    Optional<DailyExpense> findDailyExpenseByFinanceDataIdAndDate(Long financeDataId, LocalDate date);
}
