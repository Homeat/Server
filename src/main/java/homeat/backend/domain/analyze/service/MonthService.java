package homeat.backend.domain.analyze.service;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.homeatreport.entity.Week_Analyze;
import homeat.backend.domain.homeatreport.entity.Week_Check;
import homeat.backend.domain.homeatreport.repository.WeekAnalyzeRepository;
import homeat.backend.domain.homeatreport.repository.WeekCheckRepository;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MonthService {

    private final MemberRepository memberRepository;
    private final FinanceDataRepository financeDataRepository;
    private final WeekCheckRepository weekCheckRepository;
    private final WeekAnalyzeRepository weekAnalyzeRepository;

    /**
     * 매달 1일, default row 생성
     */

    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void createMonthlyFinanceData() {
        List<Member> members = memberRepository.findAll();
        LocalDate today = LocalDate.now();
        LocalDate lastMonth = today.minusMonths(1);

        members.forEach(member -> {
            Optional<FinanceData> existingFinanceData =
                    financeDataRepository.findByMemberAndCreatedAt(member, today);

            // 회원가입 날짜가 1일 경우 예외처리
            if (existingFinanceData.isEmpty()) {

                Optional<FinanceData> lastMonthFinanceData =
                        financeDataRepository.findByMemberAndCreatedAt(member, lastMonth);

                Long numHomeatBadge = 0L;
                if (lastMonthFinanceData.isPresent()) {
                    numHomeatBadge = lastMonthFinanceData.get().getNum_homeat_badge();
                }

                FinanceData financeData = FinanceData.builder()
                        .member(member)
                        .num_homeat_badge(numHomeatBadge)
                        .build();

                FinanceData savedfinanceData = financeDataRepository.save(financeData);

                LocalDateTime startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).atStartOfDay();
                LocalDateTime endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).atStartOfDay();

                if (startOfWeek.getMonth() != endOfWeek.getMonth()) {
                    List<Week_Check> weekChecks = weekCheckRepository.findAllByCreatedAtBetween(startOfWeek, endOfWeek);
                    List<Week_Analyze> weekAnalyzes = weekAnalyzeRepository.findAllByCreatedAtBetween(startOfWeek, endOfWeek);
                    // weekCheck의 financeData 업데이트
                    weekChecks.forEach(weekCheck -> {
                        weekCheck.updateFinanceData(savedfinanceData);
                        weekCheckRepository.save(weekCheck);
                    });

                    // weekAnalyze의 financeData 업데이트
                    weekAnalyzes.forEach(weekAnalyze -> {
                        weekAnalyze.updateFinanceData(savedfinanceData);
                        weekAnalyzeRepository.save(weekAnalyze);
                    });
                }
            }
        });
    }
}