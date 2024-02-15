package homeat.backend.domain.homeatreport.repository.querydsl;

import homeat.backend.domain.homeatreport.entity.Week;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface WeekRepositoryCustom {
    Optional<Week> findWeekByMemberIdAndFinanceDataId(Long member_id);

    Slice<Week> findWeekByMemberIdAsc(Long member_id, Long lastWeekId, Pageable pageable);
}
