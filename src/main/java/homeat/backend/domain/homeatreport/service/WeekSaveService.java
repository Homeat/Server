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

    public Long accumulatePrice() {

        LocalDate today = LocalDate.now();
        DayOfWeek todayOfWeek = today.getDayOfWeek();

        LocalDate recentSunday;
        if (todayOfWeek == DayOfWeek.SUNDAY) {
            recentSunday = today;
            System.out.println("최근 일요일: "+recentSunday);
        } else {
            recentSunday = today.minusDays(todayOfWeek.getValue());
            System.out.println("최근 일요일: "+recentSunday);
        }

        // 가장 최근의 일요일부터 오늘까지의 DailyExpense list
//        return dailyExpenseRepository.sumPricesBetweenDates(recentSunday, today);
        return 0L;
    }


    public void saveWeek(Week week) {
        // week 엔티티의 exceedPrice 업데이트
        Long exceedPrice = accumulatePrice() - week.getGoal_price();
        week.updateExceedPrice(exceedPrice);

        System.out.println("exceed price here");
        // week 엔티티 저장
        weekRepository.save(week);
    }

}
