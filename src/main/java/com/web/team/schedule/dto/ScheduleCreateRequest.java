package com.web.team.schedule.dto;

import com.web.team.schedule.domain.ScheduleType;

import java.time.LocalDateTime;

public record ScheduleCreateRequest (
        String title,
        String content,
        LocalDateTime startedAt,
        LocalDateTime endAt,
        ScheduleType type
        )
            {
}
