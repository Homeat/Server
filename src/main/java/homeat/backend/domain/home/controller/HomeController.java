package homeat.backend.domain.home.controller;

import homeat.backend.domain.home.dto.HomeRequestDTO;
import homeat.backend.domain.home.service.HomeService;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;
    private final MemberQueryService memberQueryService;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * 목표 금액 수정 (수정하면 다음 주 목표식비에 반영)
     */
    @Operation(summary = "홈 화면 목표 금액 수정 api")
    @PatchMapping("/target-expense")
    public ResponseEntity<?> updateTargetExpense(
            @RequestBody HomeRequestDTO.TargetExpenseDTO dto,
            Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return homeService.updateTargetExpense(dto, member);
    }

    /**
     * 홈 화면 조회
     * 목표 금액 0 원 -> 닉네임, 현재 누적 뱃지 개수
     * 목표 금액 0 원 이상 -> 닉네임, 현재 누적 뱃지 개수, 목표금액, 지난 주(과거 사용 금액) 대비 이번 주 절약 퍼센트, 이번 주 사용 금액, 목표금액 대비 이번 주 사용 퍼센트 반환
     */
    @GetMapping("/")
    public ResponseEntity<?> getHome(Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return ResponseEntity.ok(homeService.getHome(member));
    }

    /**
     *  OCR 영수증 처리 (끝 -> 다른 양식은 어떻게 할 지 고민해보기)
     */
    @Operation(summary = "영수증 추출(ocr) API")
    @PostMapping(value = "/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> processReceipt(@RequestParam("file") MultipartFile file) {
        try {
            Long totalExpense = homeService.processReceiptAndSaveExpense(file);
            return ResponseEntity.ok("총 금액이 저장됐습니다 : " + totalExpense);
        } catch (IOException e) {
            logger.error("영수증 처리 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("영수증 처리 에러");
        }
    }

    /**
     * 지출 추가
     * OCR로 금액 추출뿐만 아니라 직접 수기로 금액 작성 가능
     */
    @Operation(summary = "지출 추가 api")
    @PostMapping("/add-expense")
    public ResponseEntity<?> createReceipt(@RequestBody HomeRequestDTO.ReceiptDTO dto,
                                           Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return homeService.createReceipt(dto, member);
    }

    /**
     * 지출 확인(해당 월 데이터) -> 하루 데이터를 리스트로 전송(목표 금액은 없음)
     */
    @Operation(summary = "연월별 데이터 조회 api")
    @GetMapping("/calendar")
    public ResponseEntity<?> getCalendar(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            Authentication authentication) {
        Member member = memberQueryService.mypageMember(Long.parseLong(authentication.getName()));
        return homeService.getCalendar(year, month, member);
    }

    /**
     * 지출 확인(일일 데이터) -> 해당 날짜 주의 목표 식비 - 해당 날짜 사용 금액
     */
}