package com.web.team.schedule.domain;

import com.web.team.admin.domain.Admin;
import com.web.team.exception.CustomException;
import com.web.team.exception.ErrorCode;
import com.web.team.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자, 외부 생성 차단
public class Schedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 스케줄 ID

    private String title; // 일정 제목

    private String content; // 일정 내용

    private LocalDateTime startedAt; // 시작 시간

    private LocalDateTime endAt; // 종료 시간

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at; // 생성 시간

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updated_at; // 수정 시간

    private Boolean is_company; // 회사 일정, 개인 일정 ( True = 회사, false = 개인 )

    @Enumerated(value = EnumType.STRING)
    private ScheduleType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 관리자용 일정 생성
    public static Schedule create(
            String title, String content, LocalDateTime startedAt, LocalDateTime endAt,
            ScheduleType type, Admin admin
    ) {

            validateTimePeriod(startedAt, endAt);
            if (admin == null) throw new CustomException(ErrorCode.INVALID_ADMIN);

            Schedule schedule = new Schedule();
            schedule.title = title;
            schedule.content = content;
            schedule.startedAt = startedAt;
            schedule.endAt = endAt;
            schedule.type = type;
            schedule.is_company = true;
            schedule.admin = admin;
            return schedule;
    }

    // 직원용 일정 생성
    public static Schedule create(
            String title, String content, LocalDateTime startedAt, LocalDateTime endAt,
            ScheduleType type, User user
    ) {

        validateTimePeriod(startedAt, endAt);
        if (user == null) throw new CustomException(ErrorCode.INVALID_USER);

        Schedule schedule = new Schedule();
        schedule.title = title;
        schedule.content = content;
        schedule.startedAt = startedAt;
        schedule.endAt = endAt;
        schedule.type = type;
        schedule.is_company = false;
        schedule.user = user;
        return schedule;
    }

    public void update(String title, String content, LocalDateTime startedAt, LocalDateTime endAt, ScheduleType type) {
        if (startedAt != null && endAt != null) validateTimePeriod(startedAt, endAt);
        if ( title != null) this.title = title;
        if ( content != null) this.content = content;
        if ( startedAt != null) this.startedAt = startedAt;
        if ( endAt != null) this.endAt = endAt;
        if ( type != null) this.type = type;
        
    }

    public void validateAdminOwner(Admin admin) {
        if (this.admin == null || admin == null || !this.admin.getId().equals(admin.getId())) {
            throw new CustomException(ErrorCode.SCHEDULE_FORBIDDEN);
        }
    }

    public void validateUserOwner(User user) {
        if (this.user == null || user == null || !this.user.getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.SCHEDULE_FORBIDDEN);
        }
    }

    // 유효 시간 검사
    private static void validateTimePeriod(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) throw new CustomException(ErrorCode.INVALID_SCHEDULE_TIME);
    }


}
