package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.entity.WeekStatus;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class WeekGenerationService {

    private final WeekRepository weekRepository;
    private final FinanceDataRepository financeDataRepository;

    /**
     *  1. 매주 일요일에 week 엔티티 새로 생성
     *  2. 직전 week 엔티티의 달성 여부, 홈잇티어, badge 이미지 최신화
     *  3. week 엔티티 생성 시점에 month가 바뀌었으면 financedata 엔티티 새로 생성
     */

    // 회원가입 시점이 일요일 00시 00분인 경우 예외처리 필요
    @Scheduled(cron = "0 0 0 * * SUN")
    public void generateNewWeek() {
        Week newWeek = Week.builder().build();

        weekRepository.save(newWeek);



        // 직전 week 찾기
        Optional<Week> optionalPreviousWeek = weekRepository.findById(newWeek.getId() - 1);
        if (optionalPreviousWeek.isPresent()) {
            Week previousWeek = optionalPreviousWeek.get();

            // 이번주 달성 여부 지정 메서드
            Long previousExceedPrice = previousWeek.getExceed_price();
            int isSuccess;
            if (previousExceedPrice <= 0) {
                previousWeek.setWeekStatus(WeekStatus.SUCCESS);
                isSuccess = 1;
            } else {
                previousWeek.setWeekStatus(WeekStatus.FAIL);
                isSuccess = 0;
            }

            LocalDate now = LocalDate.now();
            // 일주일 전과 비교하여 월(month)이 바뀐 경우에 대하여
            if (now.getMonthValue() != now.minusWeeks(1).getMonthValue()) {
                FinanceData previousFinanceData = previousWeek.getFinanceData();
                Long badge_num;
                if (isSuccess == 1) { // 직전 week에서 목표 달성인 경우
                    badge_num = previousFinanceData.getNum_homeat_badge() + 1; // badge 개수 1개 추가
                } else { // 직전 week에서 목표 달성 실패인 경우
                    badge_num = previousFinanceData.getNum_homeat_badge(); // badge 개수 그대로
                }

                FinanceData newFinanceData = FinanceData.builder()
                        .num_homeat_badge(badge_num)
                        .build();
                financeDataRepository.save(newFinanceData);
            }

            // 홈잇 티어 지정 메서드
            // badge img pk 지정 메서드
        }
    }

}