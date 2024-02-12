package homeat.backend.domain.home.repository;

import homeat.backend.domain.home.entity.DailyExpense;

import java.time.LocalDate;
import java.util.List;

public interface DailyExpenseRepoCST {

    List<DailyExpense> findDailyExpenseByMemberIdAndDateBetween(Long member_id, LocalDate startOfWeek, LocalDate endOfWeek);

}
