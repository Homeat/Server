package homeat.backend.domain.homeatreport.service;

import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.analyze.repository.FinanceDataRepository;
import homeat.backend.domain.homeatreport.dto.ReportAnalyzeRequestDTO;
import homeat.backend.domain.homeatreport.dto.ReportMonthlyAnalyzeResponseDTO;
import homeat.backend.domain.homeatreport.dto.ReportWeeklyResponseDTO;
import homeat.backend.domain.user.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeatReportService {

    private final FinanceDataRepository financeDataRepository;

    // 소비분석 중 상단의 월별 분석
    public ResponseEntity<ReportMonthlyAnalyzeResponseDTO> getMonthlyAnalyze(ReportAnalyzeRequestDTO.DateInputDTO dateInputDTO, Member member) {

        Integer input_year = dateInputDTO.getInput_year();
        Integer input_month = dateInputDTO.getInput_month();

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
        return ResponseEntity.ok(reportMonthlyAnalyzeResponseDTO);
    }

    // 소비분석 중 하단의 주별 분석
    public ResponseEntity<ReportWeeklyResponseDTO> getWeeklyAnalyze(ReportAnalyzeRequestDTO.DateInputDTO dateInputDTO, Member member) {

        /**
         * 날짜에 대한 달별 주차(Week of Month)
         */
        Calendar calendar = Calendar.getInstance();

        Integer input_year = dateInputDTO.getInput_year();
        Integer input_month = dateInputDTO.getInput_month();
        Integer input_day = dateInputDTO.getInput_day();

        calendar.set(input_year, input_month - 1, input_day);
        Integer thisWeek = calendar.get(Calendar.WEEK_OF_MONTH); // 날짜에 대한 이번달 주차



    }

}
