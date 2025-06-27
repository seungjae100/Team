package com.web.team.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // Exception 메세지로 설정
        this.errorCode = errorCode;

    }
}
