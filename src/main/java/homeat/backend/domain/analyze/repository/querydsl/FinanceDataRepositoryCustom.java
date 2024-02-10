package homeat.backend.domain.analyze.repository.querydsl;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.LocalDate;
import java.util.Optional;
public interface FinanceRepositoryCustom {
    Optional<FinanceData> findByMemberAndCreatedAt(Member member, LocalDate date);
    Optional<FinanceData> findLatestFinanceDataIdByMember(Member member);
    Optional<FinanceData> findByMemberAndYearAndMonth(Member member, int year, int month);
}