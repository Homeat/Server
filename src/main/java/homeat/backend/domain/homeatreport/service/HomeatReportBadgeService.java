package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.homeatreport.dto.ReportBadgeResponseDTO;
import homeat.backend.domain.homeatreport.dto.ReportTierNicknameResponseDTO;
import homeat.backend.domain.homeatreport.entity.TierStatus;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.entity.WeekStatus;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import homeat.backend.domain.user.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeatReportBadgeService {

    private final WeekRepository weekRepository;


    // 주별 조회 상단의 회원 홈잇티어와 닉네임
    public ReportTierNicknameResponseDTO getHomeatTierNickName(Member member) {

        Optional<Week> optionalWeek = weekRepository.findWeekByMemberIdAndFinanceDataId(member.getId());
        if (optionalWeek.isEmpty()) {
            throw new RuntimeException("주(week) 엔티티를 찾을 수 없습니다.");
        }
        Week week = optionalWeek.get();
        TierStatus currentTier= week.getTierStatus();

        ReportTierNicknameResponseDTO reportTierNicknameResponseDTO = new ReportTierNicknameResponseDTO(currentTier, member.getNickname());
        return reportTierNicknameResponseDTO;

    }

    // 주별 조회 하단의 주차별 뱃지(존재하는 모든 주 list 반환)
    /*public List<ReportBadgeResponseDTO> getHomaetBadge(Member member) {
        List<Week> existWeeks = weekRepository.findAllByMemberId(member.getId(), Sort.by(Sort.Direction.ASC, "id"));
        List<ReportBadgeResponseDTO> reportBadgeResponseDTOList = new ArrayList<>(); // ReportBadgeResponseDTO 객체를 담을 리스트 생성
        for (Week week:existWeeks) {
            Long week_id = week.getId(); // week n주차
            Long goal_price = week.getGoal_price(); // 목표 금액
            Long exceed_price = week.getExceed_price(); // 초과 금액
            WeekStatus weekStatus = week.getWeek_status(); // week 달성 여부
            String badge_url = week.getBadge_img().getImage_url();
            ReportBadgeResponseDTO reportBadgeResponseDTO = new ReportBadgeResponseDTO(week_id, goal_price, exceed_price, weekStatus, badge_url);
            reportBadgeResponseDTOList.add(reportBadgeResponseDTO);
        }
        return reportBadgeResponseDTOList;
    }*/

    public Slice<Week> getHomeatBadge(Member member, Long lastWeekId) {

        Pageable pageable = PageRequest.of(0, 9);

        return weekRepository.findWeekByMemberIdAsc(member.getId(), lastWeekId, pageable);
    }


}
