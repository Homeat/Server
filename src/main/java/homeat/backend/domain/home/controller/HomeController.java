package homeat.backend.domain.home.controller;

import homeat.backend.domain.home.converter.HomeConverter;
import homeat.backend.domain.home.dto.HomeRequestDTO;
import homeat.backend.domain.home.dto.HomeResponseDTO;
import homeat.backend.domain.home.entity.DailyExpense;
import homeat.backend.domain.home.service.HomeService;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberQueryService;
import homeat.backend.global.payload.ApiPayload;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;
    private final MemberQueryService memberQueryService;

    /**
     * 목표 금액 저장
     */
    @Operation(summary = "홈 화면 목표 금액 추가 api")
    @PostMapping("/target-expense")
    public ResponseEntity<?> createTargetExpense(@RequestBody HomeRequestDTO.TargetExpenseDTO dto) {
        return homeService.createTargetExpense(dto);
    }

    /**
     * 홈 화면 조회
     */
//    @GetMapping("/")
//    public ResponseEntity<?> getHome() {
//        return homeService.;
//    }


    /**
     *  OCR 영수증 처리
     */
//    @PostMapping("/receipt")
//    public ResponseEntity<?> processReceipt(@RequestParam("file")MultipartFile file) {
//
//    }

    /**
     * 지출 확인(해당 월 데이터) -> 하루 데이터를 리스트로 전송(목표 금액은 없음)
     */
    @Operation(summary = "연월별 데이터 조회 api")
    @GetMapping("/check")
    public ResponseEntity<?> getCalendar(
            @RequestParam("year") Integer year,
            @RequestParam("month") Integer month,
            Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return homeService.getCalendar(year, month, member);
    }

    /**
     * 지출 확인(일일 데이터) -> 해당 날짜 주의 목표 식비 - 해당 날짜 사용 금액
     */
}