package homeat.backend.domain.home.service;

import homeat.backend.domain.home.converter.HomeConverter;
import homeat.backend.domain.home.dto.HomeRequestDTO;
import homeat.backend.domain.home.dto.HomeResponseDTO;
import homeat.backend.domain.home.entity.DailyExpense;
import homeat.backend.domain.home.repository.DailyExpenseRepo;
import homeat.backend.domain.home.repository.ReceiptRepo;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeService {

    private final DailyExpenseRepo dailyExpenseRepo;
    private final ReceiptRepo receiptRepo;
    private final WeekRepository weekRepository;

    /**
     *  목표 금액 저장
     */
    @Transactional
    public ResponseEntity<?> createTargetExpense(HomeRequestDTO.TargetExpenseDTO dto) {
        Week week = Week.builder()
                .goal_price(dto.getTargetExpense())
                .build();

        weekRepository.save(week);

        return ResponseEntity.ok("목표금액 저장완료");
    }

    // 홈 화면 조회
//    @Transactional
//    public ResponseEntity<?> getHome(Long )

    // 영수증 등록 후 데이터 추출
//    @Transactional

    /**
     * 지출 확인
     */
//    public ResponseEntity<List<HomeResponseDTO.CalendarResultDTO>> getCalendar(Integer year, Integer month) {
//        // 해당 연, 월의 데이터 조회
//        List<DailyExpense> calendarData = dailyExpenseRepo.findByYearAndMonth(year, month);
//        // 조회된 데이터를 DTO로 변환
//        List<HomeResponseDTO.CalendarResultDTO> result = new ArrayList<>();
//        for (DailyExpense data : calendarData) {
//            HomeResponseDTO.CalendarResultDTO dto = HomeConverter.toCalendarResult(data);
//            result.add(dto);
//        }
//        return ResponseEntity.ok(result);
//    }

}