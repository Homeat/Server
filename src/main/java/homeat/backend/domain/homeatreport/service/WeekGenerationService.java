package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.homeatreport.entity.Badge_img;
import homeat.backend.domain.homeatreport.entity.TierStatus;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.entity.WeekStatus;
import homeat.backend.domain.homeatreport.repository.BadgeImgRepository;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class WeekGenerationService {

    private final WeekRepository weekRepository;
    private final FinanceDataRepository financeDataRepository;
    private final BadgeImgRepository badgeImgRepository;
    private final MemberRepository memberRepository;

    @Scheduled(cron = "30 55 2 * * TUE")
    public void generateNewWeekMembers() {
        List<Member> members = memberRepository.findAll();
        System.out.println("members: "+members.size());

        for (Member member : members) {
            Optional<FinanceData> optionalFinanceData = financeDataRepository.findTopByMember_IdOrderByCreatedAtDesc(member.getId());
            if (optionalFinanceData.isPresent()) {
                FinanceData financeData = optionalFinanceData.get();
                generateNewWeek(financeData);
            }
            else {
                System.out.println("FinanceData for member " + member.getId() + " not found");
            }
        }

    }

    /**
     *  1. 매주 일요일에 week 엔티티 새로 생성
     *  2. 직전 week 엔티티의 달성 여부, 홈잇티어, badge 이미지 최신화
     *  3. week 엔티티 생성 시점에 month가 바뀌었으면 financedata 엔티티 새로 생성
     */

    // 회원가입 시점이 일요일 00시 00분인 경우 예외처리 필요
    public void generateNewWeek(FinanceData financeData) {

        Week newWeek = Week.builder().build(); // 1. 매주 일요일에 week 엔티티 새로 생성(일요일에 새로 시작하는 주)

        // 2. 직전 week 엔티티의 목표 달성 여부 최신화
        // 직전 week 찾기
        Week previousWeek = weekRepository.findTopByFinanceDataOrderByIdDesc(financeData)
                .orElseThrow(() -> new NoSuchElementException("No previous week found"));

        System.out.println("previousWeek pk: " + previousWeek.getId());
        FinanceData previousFinanceData = previousWeek.getFinanceData(); // 전주의 finaceData

        // 새로운 week의 목표금액을 전 week 엔티티의 next_goal_price로 지정
        Long new_goal_price = previousWeek.getNext_goal_price();

        // 새로운 week의 다음주 목표금액을 nextWeek의 goal_price와 동일하게 저장.
        // 목표금액 수정시에는 update로 변경
        Long new_next_goal_price = previousWeek.getNext_goal_price();

        // 전주의 목표 달성여부 설정
        Long previousExceedPrice = previousWeek.getExceed_price();

        int isSuccess;
        if (previousExceedPrice <= 0) {

            previousWeek.setWeekStatus(WeekStatus.SUCCESS);
            isSuccess = 1;
        }
        else {

            previousWeek.setWeekStatus(WeekStatus.FAIL);
            isSuccess = 0;
        }

        // 3. week 엔티티 생성 시점에 month가 바뀌었으면 financedata 엔티티 새로 생성
        LocalDate now = LocalDate.now();
        Long badge_num;
        // 일주일 전과 비교하여 월(month)이 바뀐 경우에 대하여
        if (now.getMonthValue() != now.minusWeeks(1).getMonthValue()) {

            if (isSuccess == 1) { // 직전 week에서 목표 달성인 경우

                badge_num = previousFinanceData.getNum_homeat_badge() + 1; // badge 개수 1개 추가

                System.out.println("badge num: "+badge_num);

                // 2. 홈잇 티어 지정 메서드
                if (badge_num <= 5 ) {

                    previousWeek.setTierStatus(TierStatus.홈잇스타터);
                } else if (badge_num <= 10) {

                    previousWeek.setTierStatus(TierStatus.홈잇러버);
                } else {

                    previousWeek.setTierStatus(TierStatus.홈잇마스터);
                }

                weekRepository.save(previousWeek);
                weekRepository.flush();
            }
            else { // 직전 week에서 목표 달성 실패인 경우

                badge_num = previousFinanceData.getNum_homeat_badge(); // badge 개수 그대로

                System.out.println("badge num: "+badge_num);
            }

            FinanceData newFinanceData = FinanceData.builder()
                    .num_homeat_badge(badge_num)
                    .build();
            financeDataRepository.save(newFinanceData);

            newWeek = newWeek.builder()
                    .goal_price(new_goal_price)
                    .next_goal_price(new_next_goal_price)
                    .financeData(newFinanceData)
                    .build();
            weekRepository.save(newWeek);

        }
        else { // 일주일 전과 비교하여 월(month)이 동일한 경우

            if (isSuccess == 1) {

                badge_num = previousFinanceData.getNum_homeat_badge() + 1;

                if (badge_num <= 5 ) {
                    previousWeek.setTierStatus(TierStatus.홈잇스타터);
                } else if (badge_num <= 10) {
                    previousWeek.setTierStatus(TierStatus.홈잇러버);
                } else {
                    previousWeek.setTierStatus(TierStatus.홈잇마스터);
                }

                weekRepository.save(previousWeek);
            }
            else { // 직전 week에서 목표 달성 실패인 경우
                badge_num = previousFinanceData.getNum_homeat_badge(); // badge 개수 그대로
            }

            previousFinanceData.setNumHomeatBadge(badge_num);
            financeDataRepository.save(previousFinanceData);

            newWeek = newWeek.builder()
                    .goal_price(new_goal_price)
                    .next_goal_price(new_next_goal_price)
                    .financeData(previousFinanceData)
                    .build();
            weekRepository.save(newWeek);
        }

        // badge img 지정 메서드
        Badge_img badgeImg = badgeImgRepository.findBadge_imgById(previousFinanceData.getNum_homeat_badge())
                .orElseThrow(() -> new RuntimeException("BadgeImg 엔티티가 존재하지 않습니다."));
        previousWeek.setBadgeImg(badgeImg);
        weekRepository.save(previousWeek);

    }

}