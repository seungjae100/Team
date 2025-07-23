package com.web.team.schedule.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.web.team.admin.domain.Admin;
import com.web.team.exception.CustomException;
import com.web.team.exception.ErrorCode;
import com.web.team.schedule.domain.Schedule;
import com.web.team.schedule.domain.ScheduleType;
import com.web.team.schedule.dto.ScheduleUpdateRequest;
import com.web.team.schedule.repository.ScheduleRepository;
import com.web.team.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceFailTest {

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;
    
    @Test
    @DisplayName("관리자 일정 조회 실패 - 존재하지 않는 일정 ID")
    void getScheduleForAdmin_fail_scheduleNotFound() {
        // given
        Long scheduleId = 1L;
        Admin admin = mock(Admin.class);

        when(scheduleRepository.findByIdForAdmin(scheduleId, admin))
        .thenReturn(Optional.empty());

        // when & then
        CustomException ex = assertThrows(CustomException.class, () ->  
                scheduleService.getScheduleForAdmin(scheduleId, admin)
        );

        // verify
        assert ex.getErrorCode() == ErrorCode.SCHEDULE_NOT_FOUND;
    }

    @Test
    @DisplayName("직원 일정 조회 실패 - 존재하지 않는 ID")
    void getScheduleForEmployee_fail_scheduleNotFound() {
        // given
        Long scheduleId = 1L;
        User user = mock(User.class);

        when(scheduleRepository.findByIdForEmployee(scheduleId, user))
        .thenReturn(Optional.empty());

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> 
            scheduleService.getScheduleForEmployee(scheduleId, user)
        );

        // verify
        assert ex.getErrorCode() == ErrorCode.SCHEDULE_NOT_FOUND;
    }

    @Test
    @DisplayName("직원 일정 수정 실패 - 존재하지 않는 ID")
    void updateSchedule_fail_scheduleNotFound() {
        // given
        Long scheduleId = 1L;
        User user = mock(User.class);

        String title = "수정된 제목";
        String content = "수정된 내용";
        LocalDateTime startedAt = LocalDateTime.of(2025, 9, 12, 14, 0);
        LocalDateTime endAt = LocalDateTime.of(2025, 9, 12, 15, 0);
        ScheduleType type = ScheduleType.COMPANY;

        when(scheduleRepository.findById(scheduleId))
        .thenReturn(Optional.empty());

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> 
            scheduleService.updateEmployeeSchedule(scheduleId,
                                                   user,
                                                   new ScheduleUpdateRequest(title, content, startedAt, endAt, type)
            )
        );

        assert ex.getErrorCode() == ErrorCode.SCHEDULE_NOT_FOUND;
    }

    @Test
    @DisplayName("직원 일정 삭제 실패 - 존재하지 않는 ID")
    void deleteEmployeeSchedule_fail_scheduleNotFound() {
        // given
        Long scheduleId = 1L;
        User user = mock(User.class);

        when(scheduleRepository.findById(scheduleId))
        .thenReturn(Optional.empty());

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> 
            scheduleService.deleteEmployeeSchedule(scheduleId, user)
        );

        assert ex.getErrorCode() == ErrorCode.SCHEDULE_NOT_FOUND;
    }

    @Test
    @DisplayName("직원 일정 삭제 실패 - 작성자가 본인이 아님")
    void deleteEmployeeSchedule_fail_unauthorized() {
        // given
        Long scheduleId = 1L;
        User writer = mock(User.class);
        User stranger = mock(User.class);

        Schedule schedule = Schedule.create(
            "제목", "내용",
            LocalDateTime.of(2025, 9, 12, 14, 0),
            LocalDateTime.of(2025, 9, 12, 16, 0),
            ScheduleType.ETC,
            writer
        );

        when(scheduleRepository.findById(scheduleId))
        .thenReturn(Optional.of(schedule));

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> 
            scheduleService.deleteEmployeeSchedule(scheduleId, stranger)
        );

        assert ex.getErrorCode() == ErrorCode.SCHEDULE_FORBIDDEN;
    }
}
