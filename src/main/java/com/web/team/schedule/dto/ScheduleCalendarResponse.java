package com.web.team.schedule.dto;

import com.web.team.schedule.domain.Schedule;
import com.web.team.schedule.domain.ScheduleType;

import java.time.LocalDateTime;

public record ScheduleCalendarResponse(
        String title,
        LocalDateTime startedAt,
        LocalDateTime endAt,
        ScheduleType type
) {
    public static ScheduleCalendarResponse from(Schedule schedule) {
        return new ScheduleCalendarResponse(
                schedule.getTitle(),
                schedule.getStartedAt(),
                schedule.getEndAt(),
                schedule.getType()
        );
    }
}
