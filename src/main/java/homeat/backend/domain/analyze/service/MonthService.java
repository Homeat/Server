package homeat.backend.domain.analyze.service;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MonthService {

    private final MemberRepository memberRepository;
    private final FinanceDataRepository financeDataRepository;

    /**
     * 매달 1일, default row 생성
     */

    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?")
    public void createMonthlyFinanceData() {
        List<Member> members = memberRepository.findAll();
        LocalDate today = LocalDate.now();

        members.forEach(member -> {
            Optional<FinanceData> existingFinanceData =
                    financeDataRepository.findByMemberAndCreatedAt(member, today);

            // 회원가입 날짜가 1일 경우 예외처리
            if (existingFinanceData.isEmpty()) {
                FinanceData financeData = FinanceData.builder()
                        .member(member)
                        .build();

                financeDataRepository.save(financeData);
            }
        });
    }
}
