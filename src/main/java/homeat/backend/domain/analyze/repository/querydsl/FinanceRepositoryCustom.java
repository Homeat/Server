package homeat.backend.domain.analyze.repository.querydsl;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.user.entity.Member;

import java.time.LocalDate;
import java.util.Optional;

public interface FinanceRepositoryCustom {
    Optional<FinanceData> findByMemberAndDate(Member member, LocalDate date);
    Optional<FinanceData> findLatestFinanceDataIdByMember(Member member);
}
