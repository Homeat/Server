package homeat.backend.domain.user.handler;

import homeat.backend.global.payload.BaseStatus;
import homeat.backend.global.payload.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorStatus implements BaseStatus {

    EXIST_EMAIL(HttpStatus.CONFLICT, "MEMBER_4090", "이미 존재하는 이메일입니다"),
    EXIST_NICKNAME(HttpStatus.CONFLICT, "MEMBER_4091", "이미 존재하는 닉네임입니다"),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_4040", "존재하지 않는 이메일입니다"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER_4010", "비밀번호가 일치하지 않습니다"),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_4041", "존재하지 않는 회원입니다"),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_4042", "존재하지 않는 주소입니다"),

    MAIL_BAD_REQUEST(HttpStatus.BAD_REQUEST, "MEMBER_4000", "메일을 전송할 수 없습니다"),
    AUTH_CODE_ERROR(HttpStatus.BAD_REQUEST, "MEMBER_4001", "잘못된 코드 알고리즘입니다"),
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
