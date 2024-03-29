package homeat.backend.domain.home.repository;

import homeat.backend.domain.home.entity.DailyExpense;
import homeat.backend.domain.home.repository.querydsl.DailyExpenseRepoCST;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyExpenseRepo extends JpaRepository<DailyExpense, Long>, DailyExpenseRepoCST {
    List<DailyExpense> findByFinanceDataIdOrderByCreatedAtAsc(Long financeDataId);
}