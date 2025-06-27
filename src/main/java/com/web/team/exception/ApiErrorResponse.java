package com.web.team.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiErrorResponse {

    @Schema(description = "에러 메세지", example = "잘못된 요청입니다.")
    private final String message;

    @Schema(description = "HTTP 상태코드", example = "400")
    private final int status;

    @Schema(description = "자세한 에러 설명", example = "입력 값이 유효하지 않습니다.")
    private final String detail;
}
