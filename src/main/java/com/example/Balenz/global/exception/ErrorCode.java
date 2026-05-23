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
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,  "토큰을 찾을 수 없습니다."),

    // User 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "회원가입 시 이메일은 필수입니다."),
    LOCAL_ACCOUNT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 일반 계정으로 가입된 이메일입니다. 일반 로그인을 진행해주세요."),
    SOCIAL_ACCOUNT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 소셜 계정으로 가입된 이메일입니다. 소셜 로그인을 진행해주세요."),

    // 소셜 로그인 관련
    INVALID_SOCIAL_PROVIDER(HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인입니다."),

    // 기사 관련
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 기사를 찾을 수 없습니다."),
    ARTICLE_SCRAP_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 기사 스크랩이 존재합니다."),

    // 키워드 관련
    KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 키워드를 찾을 수 없습니다."),
    KEYWORD_SCRAP_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 키워드 스크랩이 존재합니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
