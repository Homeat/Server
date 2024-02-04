package homeat.backend.global.security.jwt;

import homeat.backend.global.payload.BaseStatus;
import homeat.backend.global.payload.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JwtErrorStatus implements BaseStatus {

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_4010", "토큰이 올바르지 않습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_4011", "토큰이 만료되었습니다."),
    WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_4012", "토큰이 변조되거나, 로그아웃 되었습니다."),
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_4013", "토큰이 필요합니다."),
    NOT_FOUND_ID(HttpStatus.NOT_FOUND, "AUTH_4040", "찾을 수 없는(탈퇴한) 회원입니다."),
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
