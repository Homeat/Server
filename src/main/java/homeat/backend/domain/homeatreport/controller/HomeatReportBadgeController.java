
package homeat.backend.domain.homeatreport.controller;

import homeat.backend.domain.homeatreport.dto.ReportBadgeResponseDTO;
import homeat.backend.domain.homeatreport.service.HomeatReportBadgeService;
import homeat.backend.domain.user.dto.CustomUserDetails;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberQueryService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/badgeReport")
@RequiredArgsConstructor
public class HomeatReportBadgeController {

    private final HomeatReportBadgeService homeatReportBadgeService;
    private final MemberQueryService memberQueryService;

    @Operation(summary = "홈잇리포트 주별조회 홈잇티어 및 닉네임, 주별 뱃지데이터 표시 api")
    @GetMapping("/Badge")
    public ApiPayload<List<ReportBadgeResponseDTO>> getHomeatBadgeController(
            @RequestParam Long lastWeekId,
            @AuthenticationPrincipal CustomUserDetails authentication
    ) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());

        return ApiPayload.onSuccess(CommonSuccessStatus.OK, homeatReportBadgeService.getHomeatBadge(member, lastWeekId));
    }
}
