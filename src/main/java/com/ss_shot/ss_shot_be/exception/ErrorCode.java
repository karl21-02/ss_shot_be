package com.ss_shot.ss_shot_be.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth Errors (401)
    AUTH_001(HttpStatus.UNAUTHORIZED, "AUTH_001", "토큰이 없습니다."),
    AUTH_002(HttpStatus.UNAUTHORIZED, "AUTH_002", "토큰이 만료되었습니다."),
    AUTH_003(HttpStatus.UNAUTHORIZED, "AUTH_003", "유효하지 않은 토큰입니다."),
    AUTH_004(HttpStatus.UNAUTHORIZED, "AUTH_004", "소셜 로그인에 실패했습니다."),

    // Sync Errors (400)
    SYNC_001(HttpStatus.BAD_REQUEST, "SYNC_001", "동기화 데이터 형식이 올바르지 않습니다."),

    // Search Errors (400)
    SEARCH_001(HttpStatus.BAD_REQUEST, "SEARCH_001", "검색어는 최소 2자 이상이어야 합니다."),

    // Not Found (404)
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    SCREENSHOT_NOT_FOUND(HttpStatus.NOT_FOUND, "SCREENSHOT_NOT_FOUND", "스크린샷을 찾을 수 없습니다."),

    // Server Errors (500)
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
