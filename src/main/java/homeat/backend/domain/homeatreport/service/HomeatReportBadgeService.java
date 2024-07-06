package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.homeatreport.controller.HomeatReportErrorStatus;
import homeat.backend.domain.homeatreport.dto.ReportBadgeImgResponseDTO;
import homeat.backend.domain.homeatreport.dto.ReportBadgeInfoResponseDTO;
import homeat.backend.domain.homeatreport.entity.TierStatus;
import homeat.backend.domain.homeatreport.entity.WeekCheck;
import homeat.backend.domain.homeatreport.repository.querydsl.WeekRepositoryCustom;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.repository.MemberInfoRepository;
import homeat.backend.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeatReportBadgeService {

    private final WeekRepositoryCustom weekRepositoryCustom;
    private final MemberInfoRepository memberInfoRepository;

    public List<ReportBadgeImgResponseDTO> getHomeatBadgeImg(Member member, Long lastWeekId) {

        Pageable pageable = PageRequest.of(0, 9);
        Slice<WeekCheck> weekCheckPage = weekRepositoryCustom.findWeekByMemberIdAsc(member.getId(), lastWeekId, pageable);
        List<ReportBadgeImgResponseDTO> reportBadgeImgResponseDTOList = weekCheckPage.getContent().stream()
                .map(week -> new ReportBadgeImgResponseDTO(
                        week.getId(),
                        week.getGoal_price(),
                        week.getExceed_price(),
                        week.getWeekStatus(),
                        week.getBadge_img().getImage_url()
                ))
                .collect(Collectors.toList());

        return reportBadgeImgResponseDTOList;
    }

    public ReportBadgeInfoResponseDTO getHomeatBadgeInfo(Member member) {

        Optional<MemberInfo> optionalMemberInfo = memberInfoRepository.findMemberInfoByMember(member); // 특정 멤버의 memberInfo 엔티티
        if (optionalMemberInfo.isEmpty()) {
            throw new NotFoundException("Member Not Exist");
        }
        MemberInfo memberInfo = optionalMemberInfo.get();

        System.out.println("Member's Name:" + memberInfo.getMember().getNickname());

        WeekCheck weekCheck = weekRepositoryCustom.findWeekByMemberIdOrderByWeekCheckIdDesc(member.getId())
                .orElseThrow(() -> new GeneralException(HomeatReportErrorStatus.REPORT_WEEK_CHECK_NOT_FOUND));

        TierStatus tierStatus = weekCheck.getHomeat_tier();
        String homeatTier = tierStatus.toString();

        ReportBadgeInfoResponseDTO reportBadgeInfoResponseDTO = new ReportBadgeInfoResponseDTO(homeatTier, memberInfo.getMember().getNickname());
        return reportBadgeInfoResponseDTO;

    }


    /*
    // 주별 조회 회원 홈잇티어와 닉네임, 주차별 뱃지(존재하는 모든 주 list 반환)
    public List<ReportBadgeResponseDTO> getHomaetBadge(Member member) {

        // member id를 사용하여 가장 최신에 만들어진 Week Check 엔티티를 가져옴.
        Optional<WeekCheck> optionalWeekCheck = weekRepositoryCustom.findWeekByMemberIdOrderByWeekCheckIdDesc(member.getId());
        if (optionalWeekCheck.isEmpty()) {
            throw new RuntimeException("주별조회(WeekCheck) 엔티티를 찾을 수 없습니다.");
        }

        WeekCheck weekCheck = optionalWeekCheck.get();
        TierStatus tierStatus = weekCheck.getHomeat_tier();
        String nickname = member.getNickname();


        List<WeekCheck> existWeeks = weekRepositoryCustom.findAllByMemberIdOrderByWeekCheckIdAsc(member.getId()); // 주어진 멤버의 존재하는 모든 WeekCheck 엔티티를 가져옴.
        List<ReportBadgeResponseDTO> reportBadgeResponseDTOList = new ArrayList<>(); // ReportBadgeResponseDTO 객체를 담을 리스트 생성

        for (WeekCheck week:existWeeks) {

            Long week_id = week.getId(); // week n주차
            Long goal_price = week.getGoal_price(); // 목표 금액
            Long exceed_price = week.getExceed_price(); // 초과 금액
            WeekStatus weekStatus = week.getWeekStatus(); // week 달성 여부
            String badge_url = week.getBadge_img().getImage_url(); // badge 이미지의 url
            ReportBadgeResponseDTO reportBadgeResponseDTO = new ReportBadgeResponseDTO(tierStatus.toString(), nickname, week_id, goal_price, exceed_price, weekStatus, badge_url);
            reportBadgeResponseDTOList.add(reportBadgeResponseDTO);

        }
        return reportBadgeResponseDTOList;
    }
     */

}
