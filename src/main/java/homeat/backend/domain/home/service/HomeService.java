package homeat.backend.domain.home.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import homeat.backend.domain.home.converter.HomeConverter;
import homeat.backend.domain.home.dto.HomeRequestDTO;
import homeat.backend.domain.home.dto.HomeResponseDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeService {

    private final DailyExpenseRepo dailyExpenseRepo;
    private final ReceiptRepo receiptRepo;
    private final WeekRepository weekRepository;
    private final OCRService ocrService;
    private final FileUtil fileUtil;

    private static final Logger logger = LoggerFactory.getLogger(HomeService.class);

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
    public ResponseEntity<?> createReceipt(HomeRequestDTO.ReceiptDTO dto) {
        Receipt receipt = Receipt.builder()
                .expense(dto.getMoney())
                .costType(dto.getType())
                .memo(dto.getMemo())
                .build();

        receiptRepo.save(receipt);

        return ResponseEntity.ok("지출 추가완료");
    }

    /**
     * 지출 확인
     */
    public ResponseEntity<List<HomeResponseDTO.CalendarResultDTO>> getCalendar(Integer year, Integer month, Member member) {

        // 해당 연도의 월 시작일과 마지막 날짜
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 해당 연, 월의 데이터 조회
        List<DailyExpense> calendarData = dailyExpenseRepo.findByMemberIdAndCreatedAtBetween(member.getId(), startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));

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