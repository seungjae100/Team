package com.web.team.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(
                ApiErrorResponse.builder()
                        .message("잘못된 요청입니다.")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .detail(ex.getMessage())
                        .build()
        );
    }

    // 400 파라미터 타입 불일치
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(
                ApiErrorResponse.builder()
                        .message("파라미터 형식이 올바르지 않습니다.")
                        .status(HttpStatus.BAD_REQUEST.value())
                        .detail(ex.getMessage())
                        .build()
        );
    }

    // 403 권한 없음
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiErrorResponse.builder()
                        .message("접근 권한이 없습니다.")
                        .status(HttpStatus.FORBIDDEN.value())
                        .detail(ex.getMessage())
                        .build()
        );
    }

    // 404 잘못된 URL
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiErrorResponse.builder()
                        .message("요청한 리소스를 찾을 수 없습니다.")
                        .status(HttpStatus.NOT_FOUND.value())
                        .detail(ex.getMessage())
                        .build()
        );
    }

    // 500 처리되지 않은 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAll(Exception ex) {
        return ResponseEntity.internalServerError().body(
                ApiErrorResponse.builder()
                        .message("서버 내부 오류가 발생했습니다.")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .detail(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        return ResponseEntity.status(errorCode.getStatus()).body(
                ApiErrorResponse.builder()
                        .message(errorCode.getMessage())
                        .status(errorCode.getStatus().value())
                        .detail(ex.getMessage())
                        .build()
        );
    }
}
