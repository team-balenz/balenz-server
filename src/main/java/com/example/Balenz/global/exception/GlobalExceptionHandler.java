package com.example.Balenz.global.exception;

import com.example.Balenz.global.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // BaseException (커스텀 에러)
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e) {
        ErrorCode errorCode = e.getErrorCode();
        String errorMessage = e.getMessage();

        log.error(errorCode + " - " + errorMessage);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(BaseResponse.error(errorCode, errorMessage));
    }

    // RequestBody 검증 실패한 경우 (필수 입력 항목 누락 / 형식 오류)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException - " + e.getMessage());

        FieldError fieldError = e.getBindingResult().getFieldError();

        String message = "필수 입력 항목이 누락되었거나 잘못된 형식입니다.";

        // 입력 형식 오류 시
        if (fieldError != null) {
            String code = fieldError.getCode();

            // 메시지 변경
            if ("Pattern".equals(code) || "Email".equals(code) || "Size".equals(code)
                    || "Min".equals(code) || "Max".equals(code)) {
                message = fieldError.getDefaultMessage();
            }
        }

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(BaseResponse.error(ErrorCode.INVALID_INPUT_VALUE, message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException - " + e.getMessage());

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(BaseResponse.error(ErrorCode.INVALID_INPUT_VALUE, "잘못된 쿼리 파라미터입니다."));
    }

    // 요청 본문 파싱에 실패한 경우 (Enum 값이 잘못된 경우 등)
    @ExceptionHandler(HttpMessageNotReadableException .class)
    public ResponseEntity<BaseResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException - " + e.getMessage());

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(BaseResponse.error(ErrorCode.INVALID_INPUT_VALUE, "잘못된 요청 본문입니다."));
    }

    // 쿼리 파라미터 누락된 경우
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException - " + e.getMessage());

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(BaseResponse.error(ErrorCode.INVALID_INPUT_VALUE, "쿼리 파라미터가 누락되었습니다."));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<BaseResponse<?>> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        log.error("HandlerMethodValidationException - " + e.getMessage());

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(BaseResponse.error(ErrorCode.INVALID_INPUT_VALUE, "요청 파라미터가 올바르지 않습니다."));
    }

    // 처리되지 않은 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception e) {
        log.error("Exception - " + e);

        return ResponseEntity.internalServerError()
                .body(BaseResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, "서버에서 예상치 못한 오류가 발생했습니다."));
    }

}
