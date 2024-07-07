package homeat.backend.domain.homeatreport.controller;

import homeat.backend.global.payload.BaseStatus;
import homeat.backend.global.payload.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum HomeatReportErrorStatus implements BaseStatus {

    REPORT_WEEK_ANALYZE_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_4040", "이달 Week Analyze를 찾을 수 없습니다."),
    REPORT_FINANCE_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_4041", "이달 Finance Data를 찾을 수 없습니다."),
    REPORT_PREVIOUS_FINANCE_DATA_UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "REPORT_4220", "Previous Finance Data의 날짜가 일치하지 않습니다."),
    REPORT_PREV_ZERO_EXPENSE_UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY,  "REPORT_4221", "전달 지출 기록이 없습니다."),
    REPORT_CURR_ZERO_EXPENSE_UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "REPORT_4222", "이달 지출 기록이 없습니다."),
    REPORT_MEMBER_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_4042", "비교할 회원이 존재하지 않습니다."),
    REPORT_PREV_WEEK_CHECK_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_4043", "전달 Week Check를 찾을 수 없습니다."),
    REPORT_BADGE_IMG_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_4044", "Homeat Badge의 이미지 파일을 찾을 수 없습니다."),
    REPORT_WEEK_CHECK_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_4045", "이달 Week Check를 찾을 수 없습니다."),
    REPORT_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_4046", "해당 회원이 온보딩이 완료되지 않았습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}