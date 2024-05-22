package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.homeatreport.controller.HomeatReportErrorStatus;
import homeat.backend.domain.homeatreport.entity.WeekAnalyze;
import homeat.backend.domain.homeatreport.repository.WeekAnalyzeRepository;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.repository.MemberRepository;
import homeat.backend.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class WeekAnalyzeGenerationService {
    /*
        - 회원가입 시 생성
        - 매달 1일 생성
        - 매주 월요일 생성
     */
    private final MemberRepository memberRepository;
    private final FinanceDataRepository financeDataRepository;
    private final WeekAnalyzeRepository weekAnalyzeRepository;
    private final HomeatReportAnalyzeService homeatReportAnalyzeService;

    @Scheduled(cron = "0 0 0 1 * ?") // 매달 1일 자정에 실행
    public void runOnFirstDayOfMonth() {
        generateNewWeekAnalyzeMembers();
    }

    @Scheduled(cron = "0 0 0 ? * MON") // 매주 월요일 자정에 실행
    public void runOnEveryMonday() {
        generateNewWeekAnalyzeMembers();
    }

    public void generateNewWeekAnalyzeMembers() {

        List<Member> members = memberRepository.findAll();
        System.out.println("The number of numbers: " + members.size());

        for (Member member : members) {
            Optional<FinanceData> optionalFinanceData = financeDataRepository.findTopByMember_IdOrderByCreatedAtDesc(member.getId());
            // Check: FinanceData랑 잘 매치되는지
            if (optionalFinanceData.isPresent()) { // financeData가 존재하는 경우 새로운 WeekCheck 생성
                FinanceData financeData = optionalFinanceData.get();
                generateNewWeekAnalyze(financeData);
            }
            else { // financeData가 없는 경우 해당 멤버의 id 출력
                throw new GeneralException(HomeatReportErrorStatus.REPORT_FINANCE_DATA_NOT_FOUND);
            }
        }

    }

    private void generateNewWeekAnalyze(FinanceData financeData) {

        System.out.println(financeData.getMember().getId()+"th member handling");

        Integer currentWeekIdx = homeatReportAnalyzeService.findWeekIdx(LocalDate.now()); // 생성되는 date를 기준으로 currentWeekIdx 구하기

        WeekAnalyze newWeekAnalyze = WeekAnalyze.builder()
                .financeData(financeData)
                .weekIdx(currentWeekIdx)
                .build();
        weekAnalyzeRepository.save(newWeekAnalyze);
    }
}
