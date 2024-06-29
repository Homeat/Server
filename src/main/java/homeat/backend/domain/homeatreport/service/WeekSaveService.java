package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.homeatreport.entity.WeekAnalyze;
import homeat.backend.domain.homeatreport.entity.WeekCheck;
import homeat.backend.domain.homeatreport.repository.WeekAnalyzeRepository;
import homeat.backend.domain.homeatreport.repository.WeekCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WeekSaveService {

    private final WeekCheckRepository weekCheckRepository;
    private final WeekAnalyzeRepository weekAnalyzeRepository;

    // WeekCheck 엔티티의 초과 금액 업데이트
    public void saveWeekCheck(WeekCheck weekCheck, Long accumulateExpense) {
        // WeekCheck 엔티티의 exceedPrice 업데이트
        Long exceedPrice = accumulateExpense - weekCheck.getGoal_price();
        weekCheck.updateExceedPrice(exceedPrice);

        System.out.println(weekCheck.getFinanceData().getMember().getId()+"'s exceed price update"+exceedPrice);
        weekCheckRepository.save(weekCheck);
    }

    // WeekAnalyze 엔티티의 n째주 집밥 & 배달/외식 가격 업데이트
    public void saveWeekAnalyze(WeekAnalyze weekAnalyze, Long jipbap_expense, Long out_expense) {
        // WeekAnalyze 엔티티의 week_jipbap_price 업데이트
        Long accumulate_jipbap_price = weekAnalyze.getWeek_jipbap_price() + jipbap_expense;
        weekAnalyze.setJipbapPrice(accumulate_jipbap_price);

        // WeekAnalyze 엔티티의 week_out_price 업데이트
        Long accumulate_out_price = weekAnalyze.getWeek_out_price() + out_expense;
        weekAnalyze.setOutPrice(accumulate_out_price);

        System.out.println(weekAnalyze.getFinanceData().getMember().getId()+"'s week jipbap price update" + accumulate_jipbap_price);
        System.out.println(weekAnalyze.getFinanceData().getMember().getId()+"'s week out price update" + accumulate_out_price);

        weekAnalyzeRepository.save(weekAnalyze);
    }

}
