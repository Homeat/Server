package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.home.entity.DailyExpense;
import homeat.backend.domain.home.repository.DailyExpenseRepo;
import homeat.backend.domain.homeatreport.dto.ReportMonthlyAnalyzeResponseDTO;
import homeat.backend.domain.homeatreport.dto.ReportWeeklyResponseDTO;
import homeat.backend.domain.homeatreport.dto.WeekOfDayReturn;
import homeat.backend.domain.user.entity.Gender;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.repository.MemberInfoRepository;
import homeat.backend.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeatReportAnalyzeService {

    private final FinanceDataRepository financeDataRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final MemberRepository memberRepository;
    private final DailyExpenseRepo dailyExpenseRepository;

    // 소비분석 중 상단의 월별 분석
    public ResponseEntity<ReportMonthlyAnalyzeResponseDTO> getMonthlyAnalyze(Integer input_year, Integer input_month, Member member) {

        // input year과 month에 대한 FinanceDataList
        FinanceData inputFinanceData = financeDataRepository.findByMemberIdAndCreatedYearAndCreatedMonth(member.getId(), input_year, input_month);
        Long input_month_jipbap_price = inputFinanceData.getMonth_jipbap_price();
        Long input_month_out_price = inputFinanceData.getMonth_out_price();

        double total = input_month_jipbap_price + input_month_out_price; // 총합 계산
        double jipbap_ratio = (input_month_jipbap_price / total) * 100.0; // 집밥 가격 비율 계산
        double out_ratio = (input_month_out_price / total) * 100.0; // 외식/배달 가격 비율 계산

        FinanceData previousFinanceData = null;

        Integer previous_year = input_year;
        Integer previous_month = input_month;
        Long currentId = inputFinanceData.getId();
        String save_percent = null;
        Double calc_percent = null;

        while(currentId > 0) {
            currentId--;
            if (previous_month == 1) {
                previous_year--;
                previous_month = 12;
            } else {
                previous_month--;
            }

            previousFinanceData = financeDataRepository.findByMemberIdAndCreatedYearAndCreatedMonth(member.getId(), previous_year, previous_month);

            // previousFinanceData가 존재하고 지출 합이 0이 아닌 경우, save_percent 계산 후 루프 탈출
            if (previousFinanceData != null && (previousFinanceData.getMonth_jipbap_price() + previousFinanceData.getMonth_out_price()) != 0) {
                calc_percent = 1 - (double)((input_month_jipbap_price + input_month_out_price) / (previousFinanceData.getMonth_jipbap_price() + previousFinanceData.getMonth_out_price()));
                save_percent = String.valueOf(calc_percent);
                break;
            }

            // 더 조회할 FinanceData가 없는 경우
            if (currentId <= 0) {
                save_percent = "이전 데이터가 존재하지 않습니다.";
                break;
            }
        }
        ReportMonthlyAnalyzeResponseDTO reportMonthlyAnalyzeResponseDTO = new ReportMonthlyAnalyzeResponseDTO(input_month_jipbap_price, input_month_out_price, jipbap_ratio, out_ratio, save_percent);
        return ResponseEntity.ok().body(reportMonthlyAnalyzeResponseDTO);
    }

    // 소비분석 중 하단의 주별 분석
    public ResponseEntity<ReportWeeklyResponseDTO> getWeeklyAnalyze(Integer input_year, Integer input_month, Integer input_day, Member member) {

        MemberInfo memberInfo = memberInfoRepository.findMemberInfoByMemberId(member.getId()); // 특정 멤버의 memberInfo 엔티티
        LocalDate birth = memberInfo.getBirth();
        Integer birth_year = birth.getYear();
        Integer birth_month = birth.getMonthValue();
        Integer birth_day = birth.getDayOfMonth();

        // 생년월일을 나타내는 LocalDate 객체 생성
        LocalDate birthday = LocalDate.of(birth_year, birth_month, birth_day);

        // 현재 날짜를 나타내는 LocalDate 객체 생성
        LocalDate currentDate = LocalDate.now();

        // 생년월일로부터 현재까지의 기간 계산
        Period period = Period.between(birthday, currentDate);

        // 만 나이 계산
        Integer age = period.getYears(); // 특정 멤버의 나이

        String age_range_str = age_range_calc(age); // 연령대
        Integer[] ageRange = new Integer[2];
        Integer ageQuotient = age/10;
        Integer ageRemainder = age%10;

        // 연령대 범위 계산. 초반: _0~_2, 중반: _3~_6, 후반: _7~_9
        if (ageRemainder >= 0 && ageRemainder <= 2) {
            ageRange[0] = ageQuotient * 10;
            ageRange[1] = ageQuotient * 10 + 2;
        } else if (ageRemainder >= 3 && ageRemainder <= 6) {
            ageRange[0] = ageQuotient * 10 + 3;
            ageRange[1] = ageQuotient * 10 + 6;
        } else {
            ageRange[0] = ageQuotient * 10 + 7;
            ageRange[1] = ageQuotient * 10 + 9;
        }

        Gender gender = memberInfo.getGender(); // 특정 멤버의 성별
        Long income = memberInfo.getIncome(); // 특정 멤버의 수입

        List<Member> members = memberRepository.findMemberByCriteria(ageRange, gender, income); // 특정 멤버의 연령대, 성별, 수입이 비슷한 멤버들

        WeekOfDayReturn weekOfDay = WeekOfMonth(input_year, input_month, input_day);
        LocalDate startDayOfWeek = weekOfDay.getStartOfWeek();
        LocalDate endDayOfWeek = weekOfDay.getEndOfWeek();

        Long average_jipbap_price = getDailyExpenseJipbapPrice(members, startDayOfWeek, endDayOfWeek);
        Long average_out_price = getDailyExpenseOutPrice(members, startDayOfWeek, endDayOfWeek);

        Long week_jipbap_price = 0L;
        Long week_out_price = 0L;
        List<DailyExpense> dailyExpenseList = dailyExpenseRepository.findDailyExpenseByMemberIdAndDateBetween(member.getId(), startDayOfWeek, endDayOfWeek);
        for (DailyExpense dailyExpense:dailyExpenseList) {
            week_jipbap_price += dailyExpense.getTodayJipbapPrice();
            week_out_price += dailyExpense.getTodayOutPrice();
        }

        ReportWeeklyResponseDTO reportWeeklyResponseDTO = new ReportWeeklyResponseDTO(age_range_str, income, gender, member.getNickname(), week_jipbap_price-average_jipbap_price, week_out_price-average_out_price, average_jipbap_price, week_jipbap_price, average_out_price, week_out_price);
        return ResponseEntity.ok().body(reportWeeklyResponseDTO);
    }


    public static String age_range_calc(Integer age) {

        String age_range;

        int age_quotient = age / 10;
        int age_remainder = age % 10;

        // 나이대 분류
        if (age_remainder >= 0 && age_remainder <= 2) {
            age_range = age_quotient * 10 + "대 초반";
        } else if (age_remainder >= 3 && age_remainder <= 6) {
            age_range = age_quotient * 10 + "대 중반";
        } else {
            age_range = age_quotient * 10 + "대 후반";
        }

        return age_range;
    }

    /**
     * 툭정 날짜에 대한 n주차와 주의 시작일과 마지막일 반환
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static WeekOfDayReturn WeekOfMonth(Integer year, Integer month, Integer day) {

        int idx = 0;
        Integer this_idx = 0;
        LocalDate this_startOfWeek = null;
        LocalDate this_endOfWeek = null;

        // 현재 달의 1일과 마지막 날 구하기
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        // 현재 달의 1일과 마지막 날의 주의 시작일과 마지막일 찾기
        LocalDate startOfFirstWeek = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate endOfLastWeek = lastDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

        // 주의 첫째주부터 마지막주까지의 시작일과 마지막일 출력
        LocalDate startOfWeek = startOfFirstWeek;
        while (!startOfWeek.isAfter(endOfLastWeek)) {
            idx++;
            LocalDate endOfWeek = startOfWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

            if (startOfWeek.getMonthValue() != month) { // 1일을 포함하는 주가 전달과 겹치는 경우
                startOfWeek = startOfWeek.with(TemporalAdjusters.firstDayOfNextMonth());
            } else if (endOfWeek.getMonthValue() != month) { // 첫째주가 아니면서 주의 마지막 날의 month가 다음달인 경우
                endOfWeek = LocalDate.of(year, month, lastDayOfMonth.getDayOfMonth());
            }

            LocalDate input_date = LocalDate.of(year, month, day);
            if (input_date.isAfter(startOfWeek) & input_date.isBefore(endOfWeek)) {
                System.out.println(idx + "번째주");
                System.out.println("Start of Week: " + startOfWeek + " - End of Week: " + endOfWeek);
                this_idx = idx;
                this_startOfWeek = startOfWeek;
                this_endOfWeek = endOfWeek;

            }

            if (startOfWeek.getMonthValue() > month) {
                break;
            }

            startOfWeek = endOfWeek.plusDays(1);

        }
        return new WeekOfDayReturn(this_idx, this_startOfWeek, this_endOfWeek);
    }

    /**
     * members 평균 집밥 지출량
     */
    public Long getDailyExpenseJipbapPrice(List<Member> members, LocalDate startOfWeek, LocalDate endOfWeek) {
        Long jipbap_prices = 0L;

        for (Member member : members) {
            List<DailyExpense> dailyExpenseList = dailyExpenseRepository.findDailyExpenseByMemberIdAndDateBetween(member.getId(), startOfWeek, endOfWeek);
            for (DailyExpense dailyExpense:dailyExpenseList) {
                jipbap_prices += dailyExpense.getTodayJipbapPrice();
            }
        }
        jipbap_prices = jipbap_prices/ members.size();
        return jipbap_prices;
    }

    /**
     * members 평균 외식/배달 지출량
     */
    public Long getDailyExpenseOutPrice(List<Member> members, LocalDate startOfWeek, LocalDate endOfWeek) {
        Long out_prices = 0L;

        for (Member member : members) {
            List<DailyExpense> dailyExpenseList = dailyExpenseRepository.findDailyExpenseByMemberIdAndDateBetween(member.getId(), startOfWeek, endOfWeek);
            for (DailyExpense dailyExpense:dailyExpenseList) {
                out_prices += dailyExpense.getTodayOutPrice();
            }
        }
        out_prices = out_prices/ members.size();
        return out_prices;
    }

}
