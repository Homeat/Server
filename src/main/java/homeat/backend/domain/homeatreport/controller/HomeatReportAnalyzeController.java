package homeat.backend.domain.homeatreport.controller;

import homeat.backend.domain.homeatreport.dto.ReportMonthlyAnalyzeResponseDTO;
import homeat.backend.domain.homeatreport.dto.ReportWeeklyResponseDTO;
import homeat.backend.domain.homeatreport.service.HomeatReportAnalyzeService;
import homeat.backend.domain.user.dto.CustomUserDetails;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberQueryService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/homeatReport")
@RequiredArgsConstructor
public class HomeatReportAnalyzeController {

    private final HomeatReportAnalyzeService homeatReportAnalyzeService;
    private final MemberQueryService memberQueryService;

    /**
     * 소비분석 월별
     * @param input_year
     * @param input_month
     * @param authentication
     * @return
     */

    @Operation(summary = "홈잇리포트 소비분석 상단의 날짜 조회 api")
    @GetMapping("/ofMonth")
    public ApiPayload<ReportMonthlyAnalyzeResponseDTO> getMonthInput(
            @RequestParam(value = "input_year", defaultValue = "#{T(java.time.LocalDate).now().getYear()}") String input_year,
            @RequestParam(value = "input_month", defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") String input_month,
            @AuthenticationPrincipal CustomUserDetails authentication
    ) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());

        Integer year = Integer.parseInt(input_year);
        Integer month = Integer.parseInt(input_month);

        return ApiPayload.onSuccess(CommonSuccessStatus.OK, homeatReportAnalyzeService.getMonthlyAnalyze(year, month, member));
    }

    /**
     * 소비분석 주별
     * @param input_year
     * @param input_month
     * @param input_day
     * @param authentication
     * @return
     */

    @Operation(summary = "홈잇리포트 소비분석 하단의 날짜 조회 api")
    @GetMapping("/ofWeek")
    public ApiPayload<ReportWeeklyResponseDTO> getWeekInput(
            @RequestParam(value = "input_year", defaultValue = "#{T(java.time.LocalDate).now().getYear()}") String input_year,
            @RequestParam(value = "input_month", defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") String input_month,
            @RequestParam(value = "input_day", defaultValue = "#{T(java.time.LocalDate).now().getDayOfMonth()}") String input_day,
            @AuthenticationPrincipal CustomUserDetails authentication
    ) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());

        Integer year = Integer.parseInt(input_year);
        Integer month = Integer.parseInt(input_month);
        Integer day = Integer.parseInt(input_day);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, homeatReportAnalyzeService.getWeeklyAnalyze(year, month, day, member));
    }
}
