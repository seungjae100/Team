package com.web.team.schedule.dto;

import com.web.team.schedule.domain.Schedule;
import com.web.team.schedule.domain.ScheduleType;

import java.time.LocalDateTime;

public record ScheduleResponse(
        Long id,
        String title,
        String content,
        LocalDateTime startedAt,
        LocalDateTime endAt,
        ScheduleType type,
        Boolean is_company
) {
    public static ScheduleResponse from(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getContent(),
                schedule.getStartedAt(),
                schedule.getEndAt(),
                schedule.getType(),
                schedule.getIs_company()
        );
    }
}
