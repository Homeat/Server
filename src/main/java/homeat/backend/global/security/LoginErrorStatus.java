package homeat.backend.global.security;

import homeat.backend.global.payload.BaseStatus;
import homeat.backend.global.payload.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LoginErrorStatus implements BaseStatus {

    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "AUTH_4000", "잘못된 파라미터 형식입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_4010", "로그인 정보가 잘못되었습니다."),
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_4011", "토큰이 존재하지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_4012", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_4013", "토큰이 올바르지 않습니다."),
    OUTPUT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_5000", "서버 출력에 오류가 있습니다. 관리자에게 문의하세요."),
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
