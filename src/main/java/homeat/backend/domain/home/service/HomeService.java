package homeat.backend.domain.home.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.home.controller.HomeConverter;
import homeat.backend.domain.home.dto.HomeRequestDTO;
import homeat.backend.domain.home.dto.HomeResponseDTO;
import homeat.backend.domain.home.entity.CostType;
import homeat.backend.domain.home.entity.DailyExpense;
import homeat.backend.domain.home.entity.Receipt;
import homeat.backend.domain.home.repository.DailyExpenseRepo;
import homeat.backend.domain.home.repository.ReceiptRepo;
import homeat.backend.domain.home.service.ocr.FileUtil;
import homeat.backend.domain.home.service.ocr.OCRService;
import homeat.backend.domain.homeatreport.entity.Week;
import homeat.backend.domain.homeatreport.repository.WeekRepository;
import homeat.backend.domain.user.entity.Member;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeService {

    private final DailyExpenseRepo dailyExpenseRepo;
    private final ReceiptRepo receiptRepo;
    private final WeekRepository weekRepository;
    private final FinanceDataRepository financeDataRepository;

    private final OCRService ocrService;
    private final FileUtil fileUtil;

    private static final Logger logger = LoggerFactory.getLogger(HomeService.class);

    /**
     *  목표 금액 수정
     */
    @Transactional
    public ResponseEntity<?> updateTargetExpense(HomeRequestDTO.TargetExpenseDTO dto, Member member) {

        // 최신 FinanceData 조회
        FinanceData financeData = financeDataRepository.findLatestFinanceDataIdByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 FinanceData가 존재하지 않습니다."));

        Week nextWeek = weekRepository.findFirstByFinanceDataOrderByCreatedAtDesc(financeData);

        nextWeek.updateGoalPrice(dto.getTargetExpense());

        return ResponseEntity.ok("목표금액 수정완료");
    }

    /**
     * 홈 화면 조회
     */
    public ResponseEntity<HomeResponseDTO.HomeResultDTO> getHome(Member member) {
        // 최근 월 데이터 조회
        FinanceData financeData = financeDataRepository.findLatestFinanceDataIdByMember(member)
                .orElseThrow(() -> new NoSuchElementException("해당 멤버는 월 데이터(finance)가 없습니다."));

        // 목표 식비 조회
        Long goalPrice = weekRepository.findTopByFinanceDataOrderByCreatedAtDesc(financeData.getId())
                .map(Week::getGoal_price)
                .orElse(0L);

        // 목표 식비가 0원 (default) -> nickname, 뱃지 개수만 반환
        HomeResponseDTO.HomeResultDTO.HomeResultDTOBuilder builder = HomeResponseDTO.HomeResultDTO.builder()
                .badgeCount(financeData.getNum_homeat_badge().intValue())
                .nickname(member.getNickname());

        // 목표 식비가 0원이 아닐 경우
        if(goalPrice > 0) {
            LocalDate today = LocalDate.now();
            // 예외처리(저번 주 존재하지 않는 경우)
            LocalDate lastMonday = today.minusWeeks(1).with(DayOfWeek.MONDAY);
            LocalDate lastSunday = lastMonday.plusDays(6);
            LocalDate thisMonday = today.with(DayOfWeek.MONDAY);

            Long lastWeekTotal = dailyExpenseRepo.sumPricesBetweenDates(lastMonday, lastSunday);
            Long thisWeekTotal = dailyExpenseRepo.sumPricesBetweenDates(thisMonday, today);

            // 전주 대비 이번 주 절약 퍼센트
            int thisWeekSavingPercent = (lastWeekTotal != null && thisWeekTotal != null && lastWeekTotal != 0) ? (int) ((double) (lastWeekTotal - thisWeekTotal) / lastWeekTotal * 100) : 0;
            // 목표 금액에 대한 이번 주 남은 사용 퍼센트
            int usedPercent = 100;
            if (thisWeekTotal != null) {
                usedPercent = (int) (usedPercent - (double) thisWeekTotal / goalPrice * 100);
            }

            // 목표 금액 & 전주 대비 이번 주 절약 퍼센트 & 사용 금액 & 목표 금액 대비 사용 금액 퍼센트
            builder.targetMoney(goalPrice)
                    .lastWeekSavingPercent(thisWeekSavingPercent)
                    .usedMoney(thisWeekTotal)
                    .usedPercent(usedPercent);
        }

        HomeResponseDTO.HomeResultDTO result = builder.build();

        return ResponseEntity.ok(result);
    }

    /**
     * OCR 영수증 처리
     */
    @Transactional
    public Long processReceiptAndSaveExpense(MultipartFile file) throws IOException {
        // MultiparFile -> File
        File convertedFile = fileUtil.convertMultiPartToFile(file);

        // OCR 이미지 처리
        String ocrResult = ocrService.processImage(convertedFile.getPath());

        // OCR 결과 로그
         logger.info("OCR 결과 : {}", ocrResult);

        // 총 금액 추출
        Long totalPrice = extractTotalExpense(ocrResult);

        return totalPrice;
    }

    private Long extractTotalExpense(String ocrResult) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문잦열을 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(ocrResult);

            // totalPrice text 값 추출
            String totalPriceText = rootNode
                    .path("images")
                    .get(0)
                    .path("receipt")
                    .path("result")
                    .path("totalPrice")
                    .path("price")
                    .path("text")
                    .asText();

            // 쉼표 제거 및 Long 형변환
            Long OCRPrice = Long.parseLong(totalPriceText.replaceAll(",", ""));

            return OCRPrice;
        } catch (IOException | NumberFormatException e) {
            logger.error("총 금액 추출 중 오류 발생 : {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 지출 추가
     */
    @Transactional
    public ResponseEntity<?> createReceipt(HomeRequestDTO.ReceiptDTO dto, Member member) {
        try {
            // FinanceData 엔티티 조회
            FinanceData financeData = financeDataRepository.findLatestFinanceDataIdByMember(member)
                    .orElseThrow(() -> new NoSuchElementException("해당 멤버는 월 데이터(finance)가 없습니다."));

            // DailyExpense 엔티티 조회(없다면 생성)
            LocalDate todayDate = LocalDate.now();
            DailyExpense dailyExpense = dailyExpenseRepo.findDailyExpenseByFinanceDataIdAndDate(financeData.getId(), todayDate)
                    .orElseGet(() -> DailyExpense.builder()
                            .financeData(financeData)
                            .todayJipbapPrice(0)
                            .todayOutPrice(0)
                            .build());

            // receipt 추출
            Receipt receipt = Receipt.builder()
                    .expense(dto.getMoney())
                    .costType(dto.getType())
                    .memo(dto.getMemo())
                    .build();

            receiptRepo.save(receipt);

            // finance, daily 실시간 반영(receipt 추가되는)
            if(dto.getType() == CostType.장보기) {
                financeData.addJipbapPrice(dto.getMoney());
                dailyExpense.addJipbapPrice(dto.getMoney());
            } else if (dto.getType() == CostType.배달비 || dto.getType() == CostType.외식비) {
                financeData.addOutPrice(dto.getMoney());
                dailyExpense.addOutPrice(dto.getMoney());
            }

            financeDataRepository.save(financeData);
            dailyExpenseRepo.save(dailyExpense);

            return ResponseEntity.ok("Receipt 저장 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Receipt 저장 실패: " + e.getMessage());
        }
    }

    /**
     * 지출 확인
     */
    public ResponseEntity<List<HomeResponseDTO.CalendarResultDTO>> getCalendar(String year, String month, Member member) {

        // 해당 연, 월의 데이터 조회
        Optional<FinanceData> financeDataOpt = financeDataRepository.findByMemberAndYearAndMonth(member, year, month);

        if (financeDataOpt.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        FinanceData financeData = financeDataOpt.get();

        // DailyExpense 조회
        List<DailyExpense> calendarData = dailyExpenseRepo.findByFinanceDataId(financeData.getId());

        // 조회된 데이터를 DTO로 변환
        List<HomeResponseDTO.CalendarResultDTO> result = new ArrayList<>();
        for (DailyExpense data : calendarData) {
            if (data.getTodayJipbapPrice() != 0 || data.getTodayOutPrice() != 0) {
                HomeResponseDTO.CalendarResultDTO dto = HomeConverter.toCalendarResult(data);
                result.add(dto);
            }
        }
        return ResponseEntity.ok(result);
    }

}