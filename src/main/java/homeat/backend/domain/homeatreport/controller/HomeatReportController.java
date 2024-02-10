package homeat.backend.domain.homeatreport.controller;

import homeat.backend.domain.home.controller.HomeController;
import homeat.backend.domain.homeatreport.dto.ReportMonthlyAnalyzeRequestDTO;
import homeat.backend.domain.homeatreport.service.HomeatReportService;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/homeatReport")
@RequiredArgsConstructor
public class HomeatReportController {

    private final HomeatReportService homeatReportService;
    private final MemberQueryService memberQueryService;

    private static final Logger logger = LoggerFactory.getLogger(HomeatReportController.class);

    /**
     * 소비분석 input_year & input_month 요청
     */
    @Operation(summary = "홈잇리포트 소비분석 상단의 input_year과 input_month 조회 api")
    @GetMapping("/input-date")
    public ResponseEntity<?> getInputDate(
            @RequestBody ReportMonthlyAnalyzeRequestDTO.DateInputDTO dateInputDTO,
            Authentication authentication
    ) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return homeatReportService.getWeeklyAnalyze(dateInputDTO, member);
    }
}
