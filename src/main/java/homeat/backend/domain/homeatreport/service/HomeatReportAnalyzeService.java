package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.homeatreport.controller.HomeatReportErrorStatus;
import homeat.backend.domain.homeatreport.dto.ReportMonthlyAnalyzeResponseDTO;
import homeat.backend.domain.homeatreport.dto.ReportWeeklyResponseDTO;
import homeat.backend.domain.homeatreport.entity.WeekAnalyze;
import homeat.backend.domain.homeatreport.repository.querydsl.WeekRepositoryCustom;
import homeat.backend.domain.user.entity.Gender;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.domain.user.repository.MemberInfoRepository;
import homeat.backend.domain.user.repository.MemberRepository;
import homeat.backend.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeatReportAnalyzeService {

    private final FinanceDataRepository financeDataRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final MemberRepository memberRepository;
    private final WeekRepositoryCustom weekRepositoryCustom;
    private final View error;

    // 소비분석 중 상단의 월별 분석
    public ReportMonthlyAnalyzeResponseDTO getMonthlyAnalyze(Integer input_year, Integer input_month, Member member) {

        // input_year과 input_month에 대한 FinanceData
        FinanceData inputFinanceData = financeDataRepository.findByMemberIdAndCreatedYearAndCreatedMonth(member.getId(), input_year, input_month)
                .orElseThrow(() -> new GeneralException(HomeatReportErrorStatus.REPORT_FINANCE_DATA_NOT_FOUND));
        Long input_month_jipbap_price = inputFinanceData.getMonth_jipbap_price();
        Long input_month_out_price = inputFinanceData.getMonth_out_price();

        /**
         * 파이 차트 비율 계산
         */
        double total = input_month_jipbap_price + input_month_out_price; // 총합
        double jipbap_ratio = (input_month_jipbap_price / total) * 100.0; // 집밥 가격 비율
        double out_ratio = (input_month_out_price / total) * 100.0; // 외식/배달 가격 비율

        int jipbap_ratio_int = (int)Math.round(jipbap_ratio);
        int out_ratio_int = (int)Math.round(out_ratio);

        Integer previous_year = input_year;
        Integer previous_month = input_month;
        Long currentId = inputFinanceData.getId();
        Double save_percent = null;

        Long previousId = currentId--;
        Optional<FinanceData> optionalPreviousFinanceData = financeDataRepository.findFinanceDataById(previousId);
        ReportMonthlyAnalyzeResponseDTO reportMonthlyAnalyzeResponseDTO;
        if (optionalPreviousFinanceData.isEmpty()) {
           throw new GeneralException(HomeatReportErrorStatus.REPORT_PREVIOUS_FINANCE_DATA_UNPROCESSABLE_ENTITY);
        }

        FinanceData previousFinanceData = optionalPreviousFinanceData.get();
        if (previous_month == 1) {
            previous_year--;
            previous_month = 12;
        } else {
            previous_month--;
        }
        // 찾은 previousFinanceData의 Date가 일치하지 않을 경우 exception
        if (previousFinanceData.getCreatedAt().getYear() == previous_year && previousFinanceData.getCreatedAt().getMonthValue() == previous_month) {
            throw new GeneralException(HomeatReportErrorStatus.REPORT_PREVIOUS_FINANCE_DATA_UNPROCESSABLE_ENTITY);
        }
        if (previousFinanceData.getMonth_jipbap_price() + previousFinanceData.getMonth_out_price() == 0) { // 이번달 지출이 없는 경우
            throw new GeneralException(HomeatReportErrorStatus.REPORT_PREV_ZERO_EXPENSE_UNPROCESSABLE_ENTITY);
        }
        if (inputFinanceData.getMonth_jipbap_price() + inputFinanceData.getMonth_out_price() == 0) { // 저번달 지출이 없는 경우
            throw new GeneralException(HomeatReportErrorStatus.REPORT_CURR_ZERO_EXPENSE_UNPROCESSABLE_ENTITY);
        }

        else {
            Long previous_month_jipbap_price = previousFinanceData.getMonth_jipbap_price();
            Long previous_month_out_price = previousFinanceData.getMonth_out_price();
            save_percent = 1 - (double)((input_month_jipbap_price + input_month_out_price) /(previous_month_jipbap_price + previous_month_out_price));
            save_percent *= 100;

            reportMonthlyAnalyzeResponseDTO = new ReportMonthlyAnalyzeResponseDTO(0, input_month_jipbap_price, input_month_out_price, jipbap_ratio_int, out_ratio_int, save_percent);
        }
        return reportMonthlyAnalyzeResponseDTO;
    }

    // 소비분석 하단의 주별 분석
    public ReportWeeklyResponseDTO getWeeklyAnalyze(Integer input_year, Integer input_month, Integer input_day, Member member) {
        MemberInfo memberInfo = memberInfoRepository.findMemberInfoByMember(member).orElseThrow(); // 특정 멤버의 memberInfo 엔티티
        System.out.println("Member's Name:" + memberInfo.getMember().getNickname());

        // 생년을 LocalDate 객체 생성
        Integer birthYear = memberInfo.getBirth().getYear();

        // 현재 year을 나타내는 LocalDate 객체 생성
        Integer currentYear = LocalDate.now().getYear();

        Integer age = currentYear - birthYear + 1; // 한국식 나이
        Integer ageIndex = age/10; // 연령대. 1이면 10대
        String ageRange = ageIndex*10+"대";

        Long income = memberInfo.getIncome(); // 특정 멤버의 수입
        String income_str = "소득 " + (income/10000) +"만원 이하";

        Gender gender = memberInfo.getGender(); // 특정 멤버의 성별
        String gender_kor = "";
        if (gender == Gender.MALE) {
            gender_kor = "남성";
        } else if (gender == Gender.FEMALE) {
            gender_kor = "여성";
        } else {
            gender_kor = " ";
        }

        // 비교군 설정
        List<Member> members = memberRepository.findMemberByCriteria(ageIndex, gender, income)
                .orElseThrow(() -> new GeneralException(HomeatReportErrorStatus.REPORT_MEMBER_GROUP_NOT_FOUND)); // 특정 멤버의 연령대, 성별, 수입이 비슷한 멤버들
        System.out.println("조건 충족 멤버 수: " + members.size());
        System.out.println(ageIndex*10 + "대 " + income_str + gender_kor);

        Long jipbapPrices = 0L;
        Long outPrices = 0L;

        // 입력된 날짜를 기준으로 해당 날짜가 포함된 주가 해당 달의 몇 번째 주인지 구하기
        LocalDate date = LocalDate.of(input_year, input_month, input_day);
        Integer weekIdx = findWeekIdx(date);


        String message = "REPORT_WEEK_ANALYZE_NOT_FOUND, " + "AgeRange: " + ageRange + ", Income: " + income_str + ", Gender: " + gender_kor + ", Nickname: " + member.getNickname(); // member는 사용자(비교군의 member가 아님)
        for (Member m : members) {
            WeekAnalyze weekAnalyze = weekRepositoryCustom.findWeekAnalyzeByMemberIdAndWeekIdxAndInputDate(m.getId(), weekIdx, input_year, input_month)
                    //.orElseThrow(() -> new GeneralException(HomeatReportErrorStatus.REPORT_WEEK_ANALYZE_NOT_FOUND));
                    .orElseThrow(() -> new RuntimeException(message));

            jipbapPrices += weekAnalyze.getWeek_jipbap_price(); // 멤버들의 집밥 가격 누적
            outPrices += weekAnalyze.getWeek_out_price(); // 멤버들의 외식 배달 가격 누적

        }
        Long average_jipbap = jipbapPrices / members.size(); // 비교군 멤버들의 평균 집밥 지출 비용
        Long average_out = outPrices / members.size(); // 비교군 멤버들의 평균 외식 배달 지출 비용

        WeekAnalyze memberWeekAnaylze = weekRepositoryCustom.findWeekAnalyzeByMemberIdAndWeekIdxAndInputDate(member.getId(), weekIdx, input_year, input_month)
                .orElseThrow(() -> new RuntimeException(message));
                //.orElseThrow(() -> new GeneralException(HomeatReportErrorStatus.REPORT_WEEK_ANALYZE_NOT_FOUND));
        Long jipbap_save = average_jipbap - memberWeekAnaylze.getWeek_jipbap_price(); // 주어진 멤버가 n째주에 절약한 집밥 비용
        Long out_save = average_out - memberWeekAnaylze.getWeek_out_price(); // 주어진 멤버가 n째주에 절약한 외식 배달 비용

        ReportWeeklyResponseDTO reportWeeklyResponseDTO = new ReportWeeklyResponseDTO(ageRange, income_str, gender_kor, member.getNickname(), jipbap_save, out_save, average_jipbap, memberWeekAnaylze.getWeek_jipbap_price(), average_out, memberWeekAnaylze.getWeek_out_price());
        return reportWeeklyResponseDTO;
    }

    /**
     * 주어진 date를 기준으로 해당 date가 포함된 주가 해당 month에서 몇 번째 주차인지 반환
     * @param date
     * @return weekIdx
     */
    public Integer findWeekIdx(LocalDate date) {
        LocalDate nextSunday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Integer weekIdx = 0;
        if (nextSunday.getMonthValue() != date.getMonthValue()) { // 주어진 date와 다음주 일요일의 month가 다를 경우(마지막 주에 다음 달로 넘어간 경우)
            LocalDate prevSunday = date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
            weekIdx = (prevSunday.getDayOfMonth() - 1) / 7 + 2; // 최근 일요일 기준 weekIdx를 구하여 1을 더함
        }
        else { // 마지막 주가 아닌 경우
            weekIdx = (nextSunday.getDayOfMonth() - 1) / 7 + 1; // 다가오는 일요일 기준 weekIdx를 구함
        }

        return weekIdx;
    }

}