package homeat.backend.domain.homeatreport.repository.querydsl;

import homeat.backend.domain.homeatreport.entity.WeekAnalyze;
import homeat.backend.domain.homeatreport.entity.WeekCheck;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface WeekRepositoryCustom {
    Optional<WeekCheck> findWeekByMemberIdOrderByWeekCheckIdDesc(Long member_id);

    Slice<WeekCheck> findWeekByMemberIdAsc(Long member_id, Long lastWeekId, Pageable pageable);

    Optional<WeekAnalyze> findWeekAnalyzeByMemberIdAndWeekIdxAndInputDate(Long memberId, Integer weekIdx, Integer input_year, Integer input_month);

    Long countPersonalWeekChecks(Long memberId);

    //List<WeekCheck> findAllByMemberIdOrderByWeekCheckIdAsc(Long memberId);

    //Optional<WeekCheck> findWeekCheckTopByMemberIdOrderByIdDesc(Long memberId);

    //Optional<WeekAnalyze> findWeekAnalyzeTopByMemberOrderByIdDesc(Long memberId);

}
