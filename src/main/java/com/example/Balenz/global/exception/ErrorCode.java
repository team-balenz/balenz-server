package com.example.Balenz.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 인증 관련
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,  "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"만료된 토큰입니다."),
    INVALID_PRINCIPAL(HttpStatus.INTERNAL_SERVER_ERROR, "유효하지 않은 인증 주체입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
