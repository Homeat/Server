package homeat.backend.domain.home.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
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
     *  다음 주 목표 금액 수정
     */
    @Transactional
    public String updateNextTargetExpense(HomeRequestDTO.nextTargetExpenseDTO dto, Member member) {

        // 최신 FinanceData 조회
        FinanceData financeData = financeDataRepository.findLatestFinanceDataIdByMember(member)
                .orElseThrow(() -> new NoSuchElementException("해당 회원의 FinanceData가 존재하지 않습니다."));

        // 다음 주 Week 조회
        Week nextWeek = weekRepository.findFirstByFinanceDataOrderByCreatedAtDesc(financeData)
                        .orElseThrow(() -> new NoSuchElementException("해당 회원의 Week가 존재하지 않습니다."));

        nextWeek.updateNextGoalPrice(dto.getNextTargetExpense());

        return "다음 주 목표금액 수정완료";
    }

    /**
     * 홈 화면 조회
     */
    public HomeResponseDTO.HomeResultDTO getHome(Member member) {
        // 최근 월 데이터 조회
        FinanceData financeData = financeDataRepository.findLatestFinanceDataIdByMember(member)
                .orElseThrow(() -> new NoSuchElementException("해당 멤버는 월 데이터(finance)가 없습니다."));

        // 목표 식비 조회(이번 주, 다음 주 데이터 조회 후 이번 주 값만 추출)
        Week thisWeek = weekRepository.findFirstByFinanceDataOrderByCreatedAtDesc(financeData)
                .orElseThrow(() -> new NoSuchElementException("해당 멤버는 Week가 존재하지 않습니다."));
        Long thisWeekGoalPrice = thisWeek.getGoal_price();

        // 목표 식비가 0원 (default) -> nickname, 뱃지 개수만 반환
        HomeResponseDTO.HomeResultDTO.HomeResultDTOBuilder builder = HomeResponseDTO.HomeResultDTO.builder()
                .badgeCount(financeData.getNum_homeat_badge().intValue())
                .nickname(member.getNickname());

        // 목표 식비가 0원이 아닐 경우
        if(thisWeekGoalPrice > 0) {
            LocalDate today = LocalDate.now();
            // 예외처리(저번 주 존재하지 않는 경우)
            LocalDate lastSunday = today.minusWeeks(1).with(DayOfWeek.SUNDAY);
            LocalDate lastSaturday = lastSunday.plusDays(6);
            LocalDate thisSunday = today.with(DayOfWeek.SUNDAY);

            // 저번 주, 이번 주 사용 금액
            Long lastWeekTotal = dailyExpenseRepo.sumPricesBetweenDates(lastSunday, lastSaturday);
            Long thisWeekTotal = dailyExpenseRepo.sumPricesBetweenDates(thisSunday, today);

            // 저번 주 금액이 0원 예외처리
            int beforeWeek = 1;
            while (lastWeekTotal == 0) {
                beforeWeek += 1;
                lastSunday = lastSunday.minusWeeks(1);
                lastSaturday = lastSunday.plusDays(6);
                lastWeekTotal = dailyExpenseRepo.sumPricesBetweenDates(lastSunday, lastSaturday);
            }

            // 전주 대비 이번 주 절약 퍼센트
            int thisWeekSavingPercent = (lastWeekTotal != null && thisWeekTotal != null && lastWeekTotal != 0) ? (int) ((double) (lastWeekTotal - thisWeekTotal) / lastWeekTotal * 100) : 0;

            // 목표 금액에 대한 이번 주 남은 사용 퍼센트
            int remainingPercent = 100;
            if (thisWeekTotal != null) {
                remainingPercent = (int) (remainingPercent - (double) thisWeekTotal / thisWeekGoalPrice * 100);
            }

            // 목표 금액 & 전주 대비 이번 주 절약 퍼센트 & 사용 금액 & 목표 금액 대비 사용 금액 퍼센트
            builder.targetMoney(thisWeekGoalPrice)
                    .beforeSavingPercent(thisWeekSavingPercent)
                    .remainingMoney(thisWeekGoalPrice - thisWeekTotal)
                    .remainingPercent(remainingPercent)
                    .beforeWeek(beforeWeek);
        }

        HomeResponseDTO.HomeResultDTO result = builder.build();

        return result;
    }

    /**
     * OCR 영수증 처리
     */
    @Transactional
    public Long processReceiptAndSaveExpense(MultipartFile file) throws IOException {
        // 파일 확장자 예외처리
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (!fileUtil.isSupportedExtension(extension)) {
            throw new IllegalAccessError(extension + " 확장자는 지원하지 않습니다. jpg, png, pdf 등만 가능");
        }

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
    public String createReceipt(HomeRequestDTO.ReceiptDTO dto, Member member) {
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

            return "영수증 저장 성공";
        } catch (Exception e) {
            throw new RuntimeException("영수증 저잘 실패 : " + e.getMessage());
        }
    }

    /**
     * 지출 확인
     */
    public List<HomeResponseDTO.CalendarResultDTO> getCalendar(String year, String month, Member member) {

        // FinanceData 조회(해당 연, 월의 데이터 조회)
        FinanceData financeData = financeDataRepository.findByMemberAndYearAndMonth(member, year, month)
                .orElseThrow(() -> new NoSuchElementException("해당 멤버는 월 데이터(finance)가 없습니다."));

        // DailyExpense 조회
        List<DailyExpense> calendarData = dailyExpenseRepo.findByFinanceDataId(financeData.getId());

        // 조회된 데이터를 DTO로 변환
        List<HomeResponseDTO.CalendarResultDTO> result = new ArrayList<>();
        for (DailyExpense data : calendarData) {
            long total = data.getTodayOutPrice() + data.getTodayJipbapPrice();
            if (total != 0) {
                int jipbapPricePercent = (int)((double)data.getTodayJipbapPrice() / total * 100);
                int outPricePercent = 100 - jipbapPricePercent;
                HomeResponseDTO.CalendarResultDTO dto = HomeResponseDTO.CalendarResultDTO.builder()
                        .date(data.getCreatedAt().toLocalDate())
                        .todayJipbapPricePercent(jipbapPricePercent)
                        .todayOutPricePercent(outPricePercent)
                        .build();
                result.add(dto);
            }
        }
        return result;
    }

    /**
     * 캘린더 하루 지출 확인
     */
    public HomeResponseDTO.CalendarDayResultDTO getCalendarDay(String year, String month, String day, Member member) {
        // FinanceData 엔티티 조회
        FinanceData financeData = financeDataRepository.findByMember_Id(member.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 멤버에 대한 FinanceData가 존재하지 않습니다."));

        // 선택 날짜의 주 계산
        LocalDate targetDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        LocalDateTime startOfWeek = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).atStartOfDay();
        LocalDateTime endOfWeek = targetDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).atTime(23, 59, 59);

        // Week 엔티티 조회
        Week week = weekRepository.findFirstByFinanceDataAndCreatedAtBetween(financeData, startOfWeek, endOfWeek)
                .orElseThrow(() -> new NoSuchElementException("해당 Week 데이터가 없습니다."));

        // 일요일부터 선택 날짜까지의 DailyExpense 모두 조회
        List<DailyExpense> dailyExpenses = dailyExpenseRepo.findDailyExpenseByMemberIdAndDateBetween(member.getId(), startOfWeek.toLocalDate(), targetDate);

        long totalUsedPrice = 0L;
        for (DailyExpense dailyExpense : dailyExpenses) {
            totalUsedPrice += dailyExpense.getTodayJipbapPrice();
            totalUsedPrice += dailyExpense.getTodayOutPrice();
        }


        // 해당 날짜의 DailyExpense 엔티티 조회
        Optional<DailyExpense> todayExpenseOpt = dailyExpenseRepo.findDailyExpenseByFinanceDataIdAndDate(financeData.getId(), targetDate);

        // DailyExpense 존재하면 값 반환, 없으면 0
        long todayJipbapPrice = todayExpenseOpt.map(DailyExpense::getTodayJipbapPrice).orElse(0L);
        long todayOutPrice = todayExpenseOpt.map(DailyExpense::getTodayOutPrice).orElse(0L);

        // 오늘 날짜는 반영이 안될 수 있으므로
        totalUsedPrice += todayJipbapPrice + todayOutPrice;

        // 총 사용 금액과 목표 금액
        long remainingGoalPrice = week.getGoal_price() - totalUsedPrice;

        HomeResponseDTO.CalendarDayResultDTO result = HomeResponseDTO.CalendarDayResultDTO.builder()
                .date(targetDate)
                .todayJipbapPrice(todayJipbapPrice)
                .todayOutPrice(todayOutPrice)
                .remainingGoal(remainingGoalPrice)
                .build();

        return result;
    }
}