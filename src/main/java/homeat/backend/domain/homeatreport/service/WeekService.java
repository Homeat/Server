package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.entity.WeekStatus;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeekService {

    private final WeekRepository weekRepository;

    /**
     * 이번주 목표식비 조회
     */
    public Long getGoalPrice(Long id) {
        Week week = weekRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 week를 찾을 수 없습니다."));
        return week.getGoal_price();
    }

    /**
     * 이번주 달성여부 조회
     */
    public WeekStatus getWeekStatus(Long id) {
        Week week = weekRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(id + " 번의 week를 찾을 수 없습니다."));
        return week.getWeek_status();
    }
}
