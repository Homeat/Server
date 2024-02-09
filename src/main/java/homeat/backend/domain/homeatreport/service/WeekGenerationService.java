package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WeekGenerationService {

    private final WeekRepository weekRepository;

    public WeekGenerationService(WeekRepository weekRepository) {
        this.weekRepository = weekRepository;
    }

    @Scheduled(cron = "0 0 0 * * SUN")
    public void generateNewWeek() {
        Week newWeek = Week.builder().build();

        weekRepository.save(newWeek);
    }
}