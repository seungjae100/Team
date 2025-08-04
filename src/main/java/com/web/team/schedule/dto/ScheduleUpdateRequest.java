package com.web.team.schedule.dto;

import com.web.team.schedule.domain.ScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "일정 수정 요청 DTO")
public record ScheduleUpdateRequest(

        @Schema(description = "일정 제목", example = "내부행사")
        String title,

        @Schema(description = "일정 내용", example = "새로운 프로젝트에 대한 발표회")
        String content,

        @Schema(description = "시작 시간", example = "2025-07-25 11:00")
        LocalDateTime startedAt,

        @Schema(description = "종료 시간", example = "2025-07-25 12:00")
        LocalDateTime endAt,

        @Schema(description = "일정 유형", example = "MEETING")
        ScheduleType type
) {

}
