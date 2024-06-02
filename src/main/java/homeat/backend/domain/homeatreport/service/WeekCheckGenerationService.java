package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.homeatreport.entity.Badge_img;
import homeat.backend.domain.homeatreport.entity.TierStatus;
import homeat.backend.domain.homeatreport.entity.WeekStatus;
import homeat.backend.domain.homeatreport.entity.Week_Check;
import homeat.backend.domain.homeatreport.repository.BadgeImgRepository;
import homeat.backend.domain.homeatreport.repository.WeekCheckRepository;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.repository.MemberInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class WeekCheckGenerationService {

    private final MemberInfoRepository memberInfoRepository;
    private final FinanceDataRepository financeDataRepository;
    private final WeekCheckRepository weekCheckRepository;
    private final BadgeImgRepository badgeImgRepository;

    @Scheduled(cron = "0 0 0 * * MON")
    public void generateNewWeekCheckMembers() {

        List<MemberInfo> memberInfos = memberInfoRepository.findAll();
        System.out.println("The number of memberInfos: " + memberInfos.size());

        for (MemberInfo memberInfo : memberInfos) {
            Long memberId = memberInfo.getMember().getId();
            Optional<FinanceData> optionalFinanceData = financeDataRepository.findTopByMember_IdOrderByCreatedAtDesc(memberId);

            // Check: FinanceData랑 잘 매치되는지
            if (optionalFinanceData.isPresent()) { // financeData가 존재하는 경우 새로운 WeekCheck 생성
                FinanceData financeData = optionalFinanceData.get();
                generateNewWeekCheck(financeData);
            } else { // financeData가 없는 경우 해당 멤버의 id 출력
                System.out.println("FinanceData for member " + memberId + " does not exist");
            }
        }
    }

    /**
     * 1. 매주 일요일에 새로운 WeekCheck 엔티티 새로 생성
     * 2. 직전 WeekCheck 엔티티의 달성 여부, 홈잇 티어, badge img 최신화
     * @param financeData
     */
    private void generateNewWeekCheck(FinanceData financeData) {

        System.out.println(financeData.getMember().getId() + "th member handling");

        // 직전 WeekCheck 데이터에 따른 새로운 WeekCheck 데이터 최신화
        // 회원가입 시, WeekCheck 엔티티가 생성되기 때문에 previousWeek가 없는 이슈 방지
        Optional<Week_Check> previousWeekCheckOpt = weekCheckRepository.findTopByFinanceDataOrderByIdDesc(financeData);

        // 직전 Week_Check가 있는 경우
        if (previousWeekCheckOpt.isPresent()) {
            Week_Check previousWeekCheck = previousWeekCheckOpt.get();

            // 새로운 Week_Check의 goal_price를 이전 주 Week_Check의 next_goal_price로 지정
            Long goal_price = previousWeekCheck.getNext_goal_price();

            // 새로운 Week_Check의 next_goal_price를 새로운 goal_price와 동일하게 지정
            Long next_goal_price = previousWeekCheck.getNext_goal_price();

            // 직전 Week_Check 엔티티의 weekStatus 결정
            FinanceData previousFinanceData = previousWeekCheck.getFinanceData();
            Long previousExceedPrice = previousWeekCheck.getExceed_price();
            Long badge_num = previousFinanceData.getNum_homeat_badge();

            if (previousExceedPrice <= 0) { // 목표 달성

                // 직전 주 홈잇 티어 최신화
                previousWeekCheck.setWeekStatus(WeekStatus.SUCCESS);

                // 현재 badge 개수 최신화
                badge_num += 1;
                financeData.setNumHomeatBadge(badge_num);
                financeDataRepository.save(financeData);

                if (badge_num <= 5) {
                    previousWeekCheck.setTierStatus(TierStatus.홈잇스타터);
                } else if (badge_num <= 10) {
                    previousWeekCheck.setTierStatus(TierStatus.홈잇러버);
                } else {
                    previousWeekCheck.setTierStatus(TierStatus.홈잇마스터);
                }

                weekCheckRepository.save(previousWeekCheck); // 직전 주 달성 여부 & 홈잇티어 설정
            } else { // 목표 실패

                previousWeekCheck.setWeekStatus(WeekStatus.FAIL);

                weekCheckRepository.save(previousWeekCheck);
            }

            Badge_img badge_img = badgeImgRepository.findBadge_imgById(badge_num);

            // 매주 월요일 00시 00분에 WeekCheck 엔티티 새로 생성(월요일~일요일이 각 엔티티 유효 기간)
            Week_Check newWeekCheck = Week_Check.builder()
                    .goal_price(goal_price)
                    .next_goal_price(next_goal_price)
                    .financeData(financeData)
                    .badge_img(badge_img)
                    .build();
            weekCheckRepository.save(newWeekCheck);

        } else { // 직전 Week_Check가 없는 경우 (회원가입 후 첫 주일 때)
            Week_Check newWeekCheck = Week_Check.builder()
                    .goal_price(0L) // 최초 목표 금액 설정
                    .next_goal_price(0L) // 최초 다음 목표 금액 설정
                    .financeData(financeData)
                    .badge_img(null) // 최초에는 badge 이미지 없음
                    .build();
            weekCheckRepository.save(newWeekCheck);
        }
    }
}
