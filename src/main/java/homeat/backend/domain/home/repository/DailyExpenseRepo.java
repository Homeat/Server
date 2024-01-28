package homeat.backend.domain.home.repository;

import homeat.backend.domain.home.entity.DailyExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyExpenseRepository extends JpaRepository<DailyExpense, Long> {
    List<DailyExpense> findByYearAndMonth(Integer year, Integer month);
}