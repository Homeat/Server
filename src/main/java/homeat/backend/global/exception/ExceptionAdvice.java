package homeat.backend.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import homeat.backend.global.payload.ApiPayload;
import homeat.backend.global.payload.ReasonDTO;
import homeat.backend.global.payload.CommonErrorStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
//@RestControllerAdvice(annotations = {RestController.class})
@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {


    @ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(constraintViolation -> constraintViolation.getMessage())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ConstraintViolationException 추출 도중 에러 발생"));

        return handleExceptionInternalConstraint(e, CommonErrorStatus.valueOf(errorMessage), HttpHeaders.EMPTY,request);
    }


    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        e.getBindingResult().getFieldErrors().stream()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
                    errors.merge(fieldName, errorMessage, (existingErrorMessage, newErrorMessage) -> existingErrorMessage + ", " + newErrorMessage);
                });

        return handleExceptionInternalArgs(e,HttpHeaders.EMPTY,CommonErrorStatus.valueOf("BAD_REQUEST"),request,errors);
    }

    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e, WebRequest request) {
        e.printStackTrace();

        return handleExceptionInternalFalse(e, CommonErrorStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY, CommonErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus(),request, e.getMessage());
    }

    @ExceptionHandler(value = GeneralException.class)
    public ResponseEntity onThrowException(GeneralException generalException, HttpServletRequest request) {
        ReasonDTO errorReasonHttpStatus = generalException.getErrorReasonHttpStatus();
        return handleExceptionInternal(generalException,errorReasonHttpStatus,null,request);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, ReasonDTO reason,
                                                           HttpHeaders headers, HttpServletRequest request) {

        ApiPayload<Object> body = ApiPayload.onFailure(reason.getCode(),reason.getMessage(),null);
//        e.printStackTrace();

        WebRequest webRequest = new ServletWebRequest(request);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                reason.getHttpStatus(),
                webRequest
        );
    }

    private ResponseEntity<Object> handleExceptionInternalFalse(Exception e, CommonErrorStatus errorStatus,
                                                                HttpHeaders headers, HttpStatus status, WebRequest request, String errorPoint) {
        ApiPayload<Object> body = ApiPayload.onFailure(errorStatus.getCode(),errorStatus.getMessage(),errorPoint);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                status,
                request
        );
    }

    private ResponseEntity<Object> handleExceptionInternalArgs(Exception e, HttpHeaders headers, CommonErrorStatus errorStatus,
                                                               WebRequest request, Map<String, String> errorArgs) {
        ApiPayload<Object> body = ApiPayload.onFailure(errorStatus.getCode(),errorStatus.getMessage(),errorArgs);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorStatus.getHttpStatus(),
                request
        );
    }

    private ResponseEntity<Object> handleExceptionInternalConstraint(Exception e, CommonErrorStatus errorStatus,
                                                                     HttpHeaders headers, WebRequest request) {
        ApiPayload<Object> body = ApiPayload.onFailure(errorStatus.getCode(), errorStatus.getMessage(), null);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorStatus.getHttpStatus(),
                request
        );
    }
}