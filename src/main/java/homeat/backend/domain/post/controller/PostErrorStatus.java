package homeat.backend.domain.post.controller;

import homeat.backend.global.payload.BaseStatus;
import homeat.backend.global.payload.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostErrorStatus implements BaseStatus {
    POST_SET_LOVE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "POST_4000", "이미 좋아요를 누른 글입니다"),
    POST_CANCEL_LOVE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "POST_4001", "이미 좋아요를 취소한 글입니다"),
    POST_REPORT_BAD_REQUEST(HttpStatus.BAD_REQUEST, "POST_4002", "이미 신고한 글입니다"),
    POST_COMMENT_REPORT_BAD_REQUEST(HttpStatus.BAD_REQUEST, "POST_4003", "이미 신고한 댓글입니다"),
    POST_REPLY_REPORT_BAD_REQUEST(HttpStatus.BAD_REQUEST, "POST_4004", "이미 신고한 대댓글입니다"),
    POST_IMAGE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "POST_4005", "사진 입력 오류"),

    POST_DELETE_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "POST_4010", "작성자가 아니라 삭제할 권한이 없습니다"),

    POST_NAME_PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED, "POST_4020", "NAME이 입력되지 않았습니다"),
    POST_MEMO_PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED, "POST_4021", "MEMO가 입력되지 않았습니다"),
    POST_TAG_PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED, "POST_4022", "TAG가 입력되지 않았습니다"),
    POST_IMAGE_PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED, "POST_4023", "IMAGE가 입력되지 않았습니다"),
    POST_ID_PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED, "POST_4024", "게시물ID가 입력되지 않았습니다"),
    POST_TITLE_PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED, "POST_4025", "TITLE이 입력되지 않았습니다"),
    POST_CONTENT_PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED, "POST_4026", "CONTENT가 입력되지 않았습니다"),
    POST_RECIPE_PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED, "POST_4027", "RECIPE가 입력되지 않았습니다"),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_4040", "존재하지 않는 게시물입니다"),
    POST_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_4041", "사진이 존재하지 않습니다"),
    POST_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_4042", "댓글이 존재하지 않습니다"),
    POST_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_4043", "대댓글이 존재하지 않습니다"),


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
