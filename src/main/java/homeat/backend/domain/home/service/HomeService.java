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
import homeat.backend.domain.homeatreport.entity.Badge_img;
import homeat.backend.domain.homeatreport.entity.Week_Check;
import homeat.backend.domain.homeatreport.repository.BadgeImgRepository;
import homeat.backend.domain.homeatreport.repository.WeekAnalyzeRepository;
import homeat.backend.domain.homeatreport.repository.WeekCheckRepository;
import homeat.backend.domain.homeatreport.service.WeekSaveService;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.global.service.S3Service;
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
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeService {

    private final DailyExpenseRepo dailyExpenseRepo;
    private final ReceiptRepo receiptRepo;
    private final WeekCheckRepository weekCheckRepository;
    private final WeekAnalyzeRepository weekAnalyzeRepository;
    private final FinanceDataRepository financeDataRepository;
    private final BadgeImgRepository badgeImgRepository;

    private final S3Service s3Service;
    private final OCRService ocrService;
    private final FileUtil fileUtil;

    private final WeekSaveService weekSaveService;

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
        Week_Check nextWeekCheck = weekCheckRepository.findFirstByFinanceDataOrderByCreatedAtDesc(financeData)
                        .orElseThrow(() -> new NoSuchElementException("해당 회원의 Week가 존재하지 않습니다."));

        nextWeekCheck.updateNextGoalPrice(dto.getNextTargetExpense());

        return "다음 주 목표금액 수정완료";
    }

    /**
     * 홈 화면 조회
     */
    public HomeResponseDTO.HomeResultDTO getHome(Member member) {
        // 최근 월 데이터 조회
        List<FinanceData> financeDataList = financeDataRepository.findTop2ByMemberOrderByCreatedAtDesc(member);
        if (financeDataList.isEmpty()) {
            throw new NoSuchElementException("해당 멤버는 월 데이터가 없습니다. 목표 식비 등 부가 정보를 입력하세요.");
        }

        // 이번 달과 저번 달 데이터
        FinanceData thisMonthFinanceData = financeDataList.get(0);
        FinanceData beforeMonthFinanceData = financeDataList.size() > 1 ? financeDataList.get(1) : null;

        // 이번 주 목표 식비 조회
        Week_Check thisWeekCheck = weekCheckRepository.findFirstByFinanceDataOrderByCreatedAtDesc(thisMonthFinanceData)
                .orElseThrow(() -> new NoSuchElementException("해당 멤버는 Week가 존재하지 않습니다."));
        Long thisWeekGoalPrice = thisWeekCheck.getGoal_price();

        int badgeCount = thisMonthFinanceData.getNum_homeat_badge().intValue();
        badgeCount = Math.min(Math.max(badgeCount, 1), 9);  // 0이면 1로, 최대 9로 설정
        Badge_img badgeImg = badgeImgRepository.findBadge_imgById((long) badgeCount);

        // 목표 식비가 0원 (default) -> nickname, 뱃지 개수만 반환
        HomeResponseDTO.HomeResultDTO.HomeResultDTOBuilder builder = HomeResponseDTO.HomeResultDTO.builder()
                .badgeImgUrl(badgeImg.getImage_url())
                .nickname(member.getNickname());

        // 목표 식비가 0원이 아닐 경우
        if(thisWeekGoalPrice > 0) {
            LocalDate today = LocalDate.now();
            // 예외처리(저번 주 존재하지 않는 경우)
            LocalDate lastSunday = today.minusWeeks(2).with(DayOfWeek.SUNDAY);
            LocalDate lastSaturday = lastSunday.plusDays(6);
            LocalDate thisSunday = today.minusWeeks(1).with(DayOfWeek.SUNDAY);

            // 저번 주, 이번 주 사용 금액
            Long lastWeekTotal = dailyExpenseRepo.sumPricesBetweenDates(lastSunday, lastSaturday, thisMonthFinanceData);
            Long thisWeekTotal = dailyExpenseRepo.sumPricesBetweenDates(thisSunday, today, thisMonthFinanceData);

            if (beforeMonthFinanceData != null) {
                lastWeekTotal += dailyExpenseRepo.sumPricesBetweenDates(lastSunday, lastSaturday, beforeMonthFinanceData);
                thisWeekTotal += dailyExpenseRepo.sumPricesBetweenDates(thisSunday, today, beforeMonthFinanceData);
            }

            if (thisWeekTotal == null) {
                thisWeekTotal = 0L;
            }

            // 저번 주 금액이 0원 예외처리
            int beforeWeek = 1;
            while (lastWeekTotal == null || lastWeekTotal == 0) {
                beforeWeek += 1;
                lastSunday = lastSunday.minusWeeks(1);
                lastSaturday = lastSunday.plusDays(6);

                Long beforeMonthTotal = beforeMonthFinanceData != null ? dailyExpenseRepo.sumPricesBetweenDates(lastSunday, lastSaturday, beforeMonthFinanceData) : 0L;
                Long thisMonthTotal = thisMonthFinanceData != null ? dailyExpenseRepo.sumPricesBetweenDates(lastSunday, lastSaturday, thisMonthFinanceData) : 0L;

                lastWeekTotal = beforeMonthTotal + thisMonthTotal;

                if (beforeWeek > 4) {
                    break;
                }
            }

            // 전주 대비 이번 주 절약 퍼센트
            int thisWeekSavingPercent = (lastWeekTotal != null && thisWeekTotal != null && lastWeekTotal != 0) ? (int) ((double) (lastWeekTotal - thisWeekTotal) / lastWeekTotal * 100) : 0;
            // 목표 금액에 대한 이번 주 남은 사용 퍼센트
            int remainingPercent = 100;
            if (thisWeekTotal != null) {
                remainingPercent = Math.max(0, (int) (remainingPercent - (double) thisWeekTotal / thisWeekGoalPrice * 100));
            }

            // 목표 금액 & 전주 대비 이번 주 절약 퍼센트 & 사용 금액 & 목표 금액 대비 사용 금액 퍼센트
            builder.targetMoney(thisWeekGoalPrice)
                    .beforeSavingPercent(thisWeekSavingPercent)
                    .remainingMoney(thisWeekGoalPrice - thisWeekTotal)
                    .remainingPercent(remainingPercent)
                    .beforeWeek(beforeWeek)
                    .message("");

            if (beforeWeek > 4) {
                builder.message("비교할 과거 데이터가 존재하지 않습니다.");
            }
        }

        HomeResponseDTO.HomeResultDTO result = builder.build();

        return result;
    }

    /**
     * OCR 영수증 처리
     */
    public HomeResponseDTO.ReceiptResultDTO processReceiptAndSaveExpense(MultipartFile file) throws IOException {
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

        String imageUrl = s3Service.uploadReceiptImg(file);

        HomeResponseDTO.ReceiptResultDTO result = new HomeResponseDTO.ReceiptResultDTO(totalPrice, imageUrl);

        return result;
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
                            .date(todayDate)
                            .build());

            // finance, daily 실시간 반영(receipt 추가되는)
            if(dto.getType() == CostType.장보기) {
                financeData.addJipbapPrice(dto.getMoney());
                dailyExpense.addJipbapPrice(dto.getMoney());
            } else if (dto.getType() == CostType.배달비 || dto.getType() == CostType.외식비) {
                financeData.addOutPrice(dto.getMoney());
                dailyExpense.addOutPrice(dto.getMoney());
            }

            dailyExpenseRepo.save(dailyExpense);

            // receipt 추출
            Receipt receipt = Receipt.builder()
                    .dailyExpense(dailyExpense)
                    .expense(dto.getMoney())
                    .costType(dto.getType())
                    .memo(dto.getMemo())
                    .url(dto.getUrl())
                    .build();

            receiptRepo.save(receipt);

            /**
             * exceed_price update
             */
            Week_Check weekCheck = weekCheckRepository.findTopByFinanceDataOrderByIdDesc(financeData)
                    .orElseThrow(() -> new NoSuchElementException("조회할 수 있는 Current Week가 없습니다."));
            Long accumulateExpense = accumulatePrice(financeData);

            weekSaveService.saveWeekCheck(weekCheck, accumulateExpense);

            financeDataRepository.save(financeData);

            return "영수증 저장 성공";
        } catch (Exception e) {
            throw new RuntimeException("영수증 저장 실패 : " + e.getMessage());
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
        List<DailyExpense> calendarData = dailyExpenseRepo.findByFinanceDataIdOrderByCreatedAtAsc(financeData.getId());

        // 조회된 데이터를 DTO로 변환
        List<HomeResponseDTO.CalendarResultDTO> result = calendarData.stream()
                .map(data -> {
                    long total = data.getTodayOutPrice() + data.getTodayJipbapPrice();
                    if (total != 0) {
                        int jipbapPricePercent = (int) ((double) data.getTodayJipbapPrice() / total * 100);
                        int outPricePercent = 100 - jipbapPricePercent;
                        return HomeResponseDTO.CalendarResultDTO.builder()
                                .date(data.getDate())
                                .todayJipbapPricePercent(jipbapPricePercent)
                                .todayOutPricePercent(outPricePercent)
                                .build();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return result;
    }

    /**
     * 캘린더 하루 지출 확인
     */
    public HomeResponseDTO.CalendarDayResultDTO getCalendarDay(String year, String month, String day, Member member) {
        LocalDate targetDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

        // 오늘 날짜
        LocalDate today = LocalDate.now();

        LocalDateTime startOfWeek = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = targetDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(23, 59, 59);

        // FinanceData 엔티티 조회
        FinanceData financeData = financeDataRepository.findByMemberAndYearAndMonth(member, year, month)
                .orElseThrow(() -> new NoSuchElementException("해당 멤버는 월 데이터가 없습니다."));

        // Week 엔티티 조회
        Week_Check weekCheck = weekCheckRepository.findFirstByFinanceDataAndCreatedAtBetween(financeData, startOfWeek, endOfWeek)
                .orElseThrow(() -> new NoSuchElementException("해당 Week 데이터가 없습니다."));

        // 해당 target 날짜 주간의 월요일 ~ 타겟날짜까지
        List<DailyExpense> weeklyExpenses = dailyExpenseRepo.findDailyExpenseByFinanceDataIdAndDateBetween(financeData.getId(), startOfWeek.toLocalDate(), targetDate);

        long totalUsedPrice = weeklyExpenses.stream()
                .mapToLong(expense -> expense.getTodayJipbapPrice() + expense.getTodayOutPrice())
                .sum();

        Optional<DailyExpense> targetExpenseOpt = dailyExpenseRepo.findDailyExpenseByFinanceDataIdAndDate(financeData.getId(), targetDate);

        // 해당 날짜 집밥 및 배달/외식 값
        long todayJipbapPrice = targetExpenseOpt.map(DailyExpense::getTodayJipbapPrice).orElse(0L);
        long todayOutPrice = targetExpenseOpt.map(DailyExpense::getTodayOutPrice).orElse(0L);

        long remainingGoalPrice = weekCheck.getGoal_price() - totalUsedPrice;

        boolean canAddExpense = !targetDate.isAfter(today) && !targetDate.isBefore(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));

        return HomeResponseDTO.CalendarDayResultDTO.builder()
                .date(targetDate)
                .todayJipbapPrice(todayJipbapPrice)
                .todayOutPrice(todayOutPrice)
                .remainingGoal(remainingGoalPrice)
                .canAddExpense(canAddExpense)
                .message("")
                .build();
    }

    /**
     * 캘린더 세부 지출 확인
     */
    public List<HomeResponseDTO.CalendarDayDetailsResultDTO> getCalendarDayDetails(String year, String month, String day, Long remainingGoal, Member member) {
        FinanceData financeData = financeDataRepository.findByMemberAndYearAndMonth(member, year, month)
                .orElseThrow(() -> new NoSuchElementException("해당 멤버는 월 데이터가 없습니다."));

        LocalDate targetDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

        DailyExpense dailyExpense = dailyExpenseRepo.findDailyExpenseByFinanceDataIdAndDate(financeData.getId(), targetDate)
                .orElseThrow(() -> new NoSuchElementException("해당 날짜의 지출 데이터가 없습니다."));

        List<Receipt> receipts = receiptRepo.findByDailyExpenseIdOrderByIdAsc(dailyExpense.getId());

        long currentRemainingGoal = remainingGoal;
        List<HomeResponseDTO.CalendarDayDetailsResultDTO> details = new ArrayList<>();
        for (Receipt receipt : receipts) {
            long usedMoney = receipt.getExpense();
            HomeResponseDTO.CalendarDayDetailsResultDTO detail = HomeResponseDTO.CalendarDayDetailsResultDTO.builder()
                    .type(receipt.getCostType())
                    .memo(receipt.getMemo())
                    .usedMoney(usedMoney)
                    .remainingGoal(currentRemainingGoal)
                    .build();
            currentRemainingGoal += usedMoney;
            details.add(detail);
        }

        return details;
    }


    @Transactional
    public String createPastExpense(HomeRequestDTO.PastExpenseDTO dto, Member member) {

        // 지출 추가 가능한 날짜인지 유효성 검증
        if (!dto.isCanAddExpense()) {
            throw new IllegalArgumentException("해당 날짜에는 지출 데이터를 추가할 수 없습니다.");
        }

        FinanceData financeData = financeDataRepository.findByMemberAndYearAndMonth(member, String.valueOf(dto.getDate().getYear()), String.valueOf(dto.getDate().getMonthValue()))
                .orElseThrow(() -> new NoSuchElementException("해당 멤버는 월 데이터가 없습니다."));

        // 해당 날짜에 기록이 없었다면 row 추가
        DailyExpense dailyExpense = dailyExpenseRepo.findDailyExpenseByFinanceDataIdAndDate(financeData.getId(), dto.getDate())
                .orElseGet(() -> DailyExpense.builder()
                        .financeData(financeData)
                        .date(dto.getDate())
                        .todayJipbapPrice(0)
                        .todayOutPrice(0)
                        .build());

        if (dto.getType() == CostType.장보기) {
            financeData.addJipbapPrice(dto.getMoney());
            dailyExpense.addJipbapPrice(dto.getMoney());
        } else if (dto.getType() == CostType.배달비 || dto.getType() == CostType.외식비) {
            financeData.addOutPrice(dto.getMoney());
            dailyExpense.addOutPrice(dto.getMoney());
        }

        dailyExpenseRepo.save(dailyExpense);
        financeDataRepository.save(financeData);

        return "과거 지출 데이터 저장 성공";
    }

    /**
     *  사용자 누적 지출 금액 계산(일요일 ~ 오늘)
     */
    public Long accumulatePrice(FinanceData financeData) {

        LocalDate today = LocalDate.now();
        DayOfWeek todayOfWeek = today.getDayOfWeek();

        LocalDate recentSunday;
        if (todayOfWeek == DayOfWeek.SUNDAY) {
            recentSunday = today;
            System.out.println("최근 일요일: "+recentSunday);
        } else {
            recentSunday = today.minusDays(todayOfWeek.getValue());
            System.out.println("최근 일요일: "+recentSunday);
        }

        // 가장 최근의 일요일부터 오늘까지의 DailyExpense list
        return dailyExpenseRepo.sumPricesBetweenDates(recentSunday, today, financeData);
    }


}