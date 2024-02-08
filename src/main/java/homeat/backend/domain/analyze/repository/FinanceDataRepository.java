package homeat.backend.domain.analyze.repository;

import homeat.backend.domain.analyze.entity.FinanceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinanceDataRepository extends JpaRepository<FinanceData, Long> {
    FinanceData findByMemberIdAndCreatedAtYearAndCreatedAtMonth(Long memberId, int year, int month);
}
