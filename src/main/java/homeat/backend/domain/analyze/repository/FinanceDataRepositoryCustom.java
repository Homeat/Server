package homeat.backend.domain.analyze.repository;

import homeat.backend.domain.analyze.entity.FinanceData;

public interface FinanceDataRepositoryCustom {
    FinanceData findByMemberIdAndCreatedYearAndCreatedMonth(Long member_id, Integer year, Integer month);
}
