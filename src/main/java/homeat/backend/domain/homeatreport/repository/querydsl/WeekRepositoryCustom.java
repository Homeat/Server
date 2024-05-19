package homeat.backend.domain.homeatreport.repository.querydsl;

import homeat.backend.domain.homeatreport.entity.Week_Analyze;
import homeat.backend.domain.homeatreport.entity.Week_Check;
import homeat.backend.domain.user.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface WeekRepositoryCustom {
    Optional<Week_Check> findWeekByMemberIdOrderByWeekCheckIdDesc(Long member_id);

    Slice<Week_Check> findWeekByMemberIdAsc(Long member_id, Long lastWeekId, Pageable pageable);

    Optional<Week_Analyze> findWeekAnalyzeByMemberIdAndWeekIdxAndInputDate(Long memberId, Integer weekIdx, Integer input_year, Integer input_month);

    List<Week_Check> findAllByMemberIdOrderByWeekCheckIdAsc(Long memberId);

    Optional<Week_Analyze> findTopByMemberOrderByIdDesc(Long memberId);

}
