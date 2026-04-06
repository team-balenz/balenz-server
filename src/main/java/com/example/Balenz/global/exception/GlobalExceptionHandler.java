package com.example.Balenz.global.exception;

import com.example.Balenz.global.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    // 처리되지 않은 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception e) {
        log.error("Exception - " + e);

        return ResponseEntity.internalServerError()
                .body(BaseResponse.error(ErrorCode.INTERNAL_SERVER_ERROR, "서버에서 예상치 못한 오류가 발생했습니다."));
    }

}
