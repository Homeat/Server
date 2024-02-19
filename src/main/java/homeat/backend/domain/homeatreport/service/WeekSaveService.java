package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.home.repository.DailyExpenseRepo;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PreUpdate;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class WeekSaveService {

    private final WeekRepository weekRepository;
    private final DailyExpenseRepo dailyExpenseRepository;

    public void saveWeek(Week week, Long accumulateExpense) {
        // week 엔티티의 exceedPrice 업데이트
        Long exceedPrice = accumulateExpense - week.getGoal_price();
        week.updateExceedPrice(exceedPrice);

        System.out.println("exceed price here");
        // week 엔티티 저장
        weekRepository.save(week);
    }

}
