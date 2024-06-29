package homeat.backend.domain.home.controller;

import homeat.backend.domain.home.dto.HomeRequestDTO;
import homeat.backend.domain.home.dto.HomeResponseDTO;
import homeat.backend.domain.home.service.HomeService;
import homeat.backend.domain.user.dto.CustomUserDetails;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.service.MemberQueryService;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.BaseStatus;
import homeat.backend.global.payload.CommonErrorStatus;
import homeat.backend.global.payload.CommonSuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    @Operation(summary = "다음 주 목표 금액 수정 api, 완료")
    @PatchMapping("/next-target-expense")
    public ApiPayload<String> updateNextTargetExpense(
            @RequestBody HomeRequestDTO.nextTargetExpenseDTO dto,
            @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        String message = homeService.updateNextTargetExpense(dto, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, message);
    }

    /**
     * 홈 화면 조회
     * 목표 금액 0 원 -> 닉네임, 현재 누적 뱃지 개수
     * 목표 금액 0 원 이상 -> 닉네임, 현재 누적 뱃지 개수, 목표금액, 지난 주(과거 사용 금액) 대비 이번 주 절약 퍼센트, 이번 주 사용 금액, 목표금액 대비 이번 주 사용 퍼센트 반환
     */
    @Operation(summary = "홈 화면 조회 api, 완료")
    @GetMapping("/")
    public ApiPayload<HomeResponseDTO.HomeResultDTO> getHome(@AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        HomeResponseDTO.HomeResultDTO result = homeService.getHome(member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     *  영수증 사진을 OCR 영수증 처리하면서 동시에 이미지 db 저장 (끝 -> 다른 양식은 어떻게 할 지 고민해보기)
     */
    @Operation(summary = "영수증 추출(ocr) API, 완료")
    @PostMapping(value = "/receipt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiPayload<HomeResponseDTO.ReceiptResultDTO> processReceipt(@RequestParam("file") MultipartFile file) {
        try {
            HomeResponseDTO.ReceiptResultDTO result = homeService.processReceiptAndSaveExpense(file);
            return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
        } catch (IOException e) {
            logger.error("영수증 처리 에러 발생", e);
            return ApiPayload.onFailure(CommonErrorStatus.INTERNAL_SERVER_ERROR.getCode(), "영수증 처리 에러", null);
        }
    }

    /**
     * 지출 추가
     * OCR로 금액 추출뿐만 아니라 직접 수기로 금액 작성 가능
     */
    @Operation(summary = "지출 추가 api, 완료")
    @PostMapping("/add-expense")
    public ApiPayload<String> createReceipt(@RequestBody HomeRequestDTO.ReceiptDTO dto,
                                            @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        String message = homeService.createReceipt(dto, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, message);
    }

    /**
     * 지출 확인(해당 월 데이터) -> 하루 데이터를 리스트로 전송(목표 금액은 없음)
     */
    @Operation(summary = "연월별 데이터 조회 api, 완료"
    , description = "기본값으로 오늘 날짜의 year, month를 받아옵니다.")
    @GetMapping("/calendar")
    public ApiPayload<List<HomeResponseDTO.CalendarResultDTO>> getCalendar(
            @RequestParam(value = "year", defaultValue = "#{T(java.time.LocalDate).now().getYear()}") String year,
            @RequestParam(value = "month", defaultValue = "#{T(java.time.LocalDate).now().getMonthValue()}") String month,
            @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        List<HomeResponseDTO.CalendarResultDTO> result = homeService.getCalendar(year, month, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 지출 확인(일일 데이터) -> 해당 날짜 주의 목표 식비 - 해당 날짜 사용 금액
     */
    @Operation(summary = "캘린더 특정 날짜 하루 데이터 조회 api, 완료")
    @GetMapping("/calendar/daily")
    public ApiPayload<HomeResponseDTO.CalendarDayResultDTO> getCalendarDay(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("day") String day,
            @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        HomeResponseDTO.CalendarDayResultDTO result = homeService.getCalendarDay(year, month, day, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 캘린더 세부 지출 확인
     */
    @Operation(summary = "세부 지출 확인 api")
    @GetMapping("/calendar/daily/details")
    public ApiPayload<List<HomeResponseDTO.CalendarDayDetailsResultDTO>> getCalendarDayDetails(
            @RequestParam("year") String year,
            @RequestParam("month") String month,
            @RequestParam("day") String day,
            @RequestParam("remainingGoal") Long remainingGoal,
            @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        List<HomeResponseDTO.CalendarDayDetailsResultDTO> result = homeService.getCalendarDayDetails(year, month, day, remainingGoal ,member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, result);
    }

    /**
     * 과거 지출 추가
     */
    @Operation(summary = "과거 지출 추가 api")
    @PostMapping("/calendar/add-expense")
    public ApiPayload<String> createPastExpense(@RequestBody HomeRequestDTO.PastExpenseDTO dto,
                                                @AuthenticationPrincipal CustomUserDetails authentication) {
        Member member = memberQueryService.mypageMember(authentication.getUserId());
        String message = homeService.createPastExpense(dto, member);
        return ApiPayload.onSuccess(CommonSuccessStatus.OK, message);
    }
}