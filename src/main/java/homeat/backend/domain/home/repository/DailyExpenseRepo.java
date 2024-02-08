package homeat.backend.domain.home.repository;

import homeat.backend.domain.home.entity.DailyExpense;
import homeat.backend.domain.home.repository.querydsl.DailyExpenseRepoCST;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyExpenseRepo extends JpaRepository<DailyExpense, Long>, DailyExpenseRepoCST {
    List<DailyExpense> findByMemberIdAndCreatedAtBetween(Long memberId, LocalDateTime localDateTime, LocalDateTime dateTime);
    Optional<DailyExpense> findByMemberId(Long memberId);
}