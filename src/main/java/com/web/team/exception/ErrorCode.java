package com.web.team.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400 Bad Request
    INVALID_INPUT_VALUE("잘못된 입력입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_EMAIL("이미 사용중인 이메일입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_BOARD_TITLE("이미 존재하는 제목입니다.", HttpStatus.BAD_REQUEST),

    // 400 UNAUTHORIZED
    UNAUTHORIZED_ACCESS("인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    LOGIN_REQUIRED("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),

    // 403 FORBIDDEN
    FORBIDDEN_ACCESS("접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    NO_PERMISSION("수정 권한이 없는 관리자입니다.", HttpStatus.FORBIDDEN),
    BOARD_PRIVATE("비공개 게시글은 조회할 수 없습니다.", HttpStatus.FORBIDDEN),
    ADMIN_ONLY_ACCESS("관리자만 접근 가능한 기능입니다.", HttpStatus.FORBIDDEN),
    SCHEDULE_FORBIDDEN("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 404 NOT FOUND
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    BOARD_NOT_FOUND("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SCHEDULE_NOT_FOUND("일정을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR("서버에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
