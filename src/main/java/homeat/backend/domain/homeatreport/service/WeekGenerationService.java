package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.entity.WeekStatus;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class WeekGenerationService {

    private final WeekRepository weekRepository;

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
            if (previousExceedPrice <= 0) {
                previousWeek.setWeekStatus(WeekStatus.SUCCESS);
            } else {
                previousWeek.setWeekStatus(WeekStatus.FAIL);
            }

            // 홈잇 티어 지정 메서드
            // badge img pk 지정 메서드
        }
    }

}