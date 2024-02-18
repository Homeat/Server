package homeat.backend.domain.homeatreport.controller;

import homeat.backend.domain.homeatreport.dto.ReportBadgeResponseDTO;
import homeat.backend.domain.homeatreport.dto.ReportTierNicknameResponseDTO;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.service.HomeatReportBadgeService;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberQueryService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/badgeReport")
@RequiredArgsConstructor
public class HomeatReportBadgeController {

    private final HomeatReportBadgeService homeatReportBadgeService;
    private final MemberQueryService memberQueryService;

    /**
     * 주별조회 티어&닉네임
     * @param authentication
     * @return
     */
    @Operation(summary = "홈잇리포트 주별조회 상단의 홈잇티어와 닉네임 표시 api")
    @GetMapping("/TierNickname")
    public ApiPayload<ReportTierNicknameResponseDTO> putTierNickname(
            Authentication authentication
    ) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, homeatReportBadgeService.getHomeatTierNickName(member));
    }

    @Operation(summary = "홈잇리포트 주별조회 하단의 주별 뱃지데이터 표시 api")
    @GetMapping("/Badge")
    public ResponseEntity<?> getHomeatBadgeController(
            @RequestParam Long lastWeekId,
            Authentication authentication
    ) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));

        return homeatReportBadgeService.getHomeatBadge(member, lastWeekId);
    }

}
