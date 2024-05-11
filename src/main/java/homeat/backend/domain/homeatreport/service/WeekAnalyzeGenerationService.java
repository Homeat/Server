package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.homeatreport.entity.Week_Analyze;
import homeat.backend.domain.homeatreport.repository.WeekAnalyzeRepository;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                System.out.println("FinanceData for member " + member.getId() + " does not exist");
            }
        }

    }

    private void generateNewWeekAnalyze(FinanceData financeData) {

        System.out.println(financeData.getMember().getId()+"th member handling");

        Week_Analyze newWeekAnalyze = Week_Analyze.builder()
                .financeData(financeData)
                .build();
        weekAnalyzeRepository.save(newWeekAnalyze);
    }
}
