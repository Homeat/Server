package homeat.backend.domain.home.controller;

import homeat.backend.domain.home.converter.HomeConverter;
import homeat.backend.domain.home.dto.HomeRequestDTO;
import homeat.backend.domain.home.dto.HomeResponseDTO;
import homeat.backend.domain.home.entity.DailyExpense;
import homeat.backend.domain.home.service.HomeService;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.global.payload.ApiPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    /**
     * 목표 금액 저장
     */
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
     *  지출(영수증) 추가 후, 데이터 저장
     */
//    @PostMapping("/receipt")
//    public ResponseEntity<?> createExpense(@RequestBody HomeRequestDTO.ReceiptDTO dto) {
//        return homeService.
//    }

    /**
     * 지출 확인(해당 월 데이터) -> 하루 데이터를 리스트로 전송(목표 금액은 없음)
     */
//    @GetMapping("/check/{year}/{month}")
//    public ResponseEntity<?> getCalendar(
//            @PathVariable("year") Integer year,
//            @PathVariable("month") Integer month) {
//        return homeService.getCalendar(year, month);
//    }

    /**
     * 지출 확인(일일 데이터) -> 해당 날짜 주의 목표 식비 - 해당 날짜 사용 금액
     */
}