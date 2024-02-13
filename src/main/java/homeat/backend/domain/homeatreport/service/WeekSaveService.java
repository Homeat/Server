package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PreUpdate;

@Service
@Transactional
@RequiredArgsConstructor
public class WeekSaveService {

    private final WeekRepository weekRepository;

    public void updateStatuses(Week week) {

    }

    public void saveWeek(Week week) {
        // week 엔티티의 exceedPrice 업데이트
        week.updateExceedPrice();

        // week 엔티티 저장
        weekRepository.save(week);
    }

    // updatedAt이 수정될 때마다 호출되는 메서드
    @PreUpdate
    public void updateWeekOnUpdate(Week week) {
        saveWeek(week);
    }

}
