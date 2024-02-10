package homeat.backend.domain.analyze.repository.querydsl;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.user.entity.Member;

import java.time.LocalDate;
import java.util.Optional;
public interface FinanceDataRepositoryCustom {
    Optional<FinanceData> findByMemberAndCreatedAt(Member member, LocalDate date);
    Optional<FinanceData> findLatestFinanceDataIdByMember(Member member);
    Optional<FinanceData> findByMemberAndYearAndMonth(Member member, String year, String month);
}