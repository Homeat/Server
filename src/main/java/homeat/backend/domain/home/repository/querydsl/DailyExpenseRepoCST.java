package homeat.backend.domain.home.repository.querydsl;

import homeat.backend.domain.home.entity.DailyExpense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyExpenseRepoCST {
    Long sumPricesBetweenDates(LocalDate startDate, LocalDate endDate);
    Optional<DailyExpense> findDailyExpenseByFinanceDataIdAndDate(Long financeDataId, LocalDate date);
    List<DailyExpense> findDailyExpenseByMemberIdAndDateBetween(Long member_id, LocalDate startOfWeek, LocalDate endOfWeek);
}
