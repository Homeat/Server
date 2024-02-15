package homeat.backend.domain.homeatreport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class WeekOfDayReturn {

    private Integer index; // n주차
    private LocalDate startOfWeek; // 주의 시작일
    private LocalDate endOfWeek; // 주의 마지막 일

}
