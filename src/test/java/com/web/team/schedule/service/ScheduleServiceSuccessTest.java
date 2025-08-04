package com.web.team.schedule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.web.team.admin.domain.Admin;
import com.web.team.schedule.domain.Schedule;
import com.web.team.schedule.domain.ScheduleType;
import com.web.team.schedule.dto.ScheduleCalendarResponse;
import com.web.team.schedule.dto.ScheduleCreateRequest;
import com.web.team.schedule.dto.ScheduleResponse;
import com.web.team.schedule.dto.ScheduleUpdateRequest;
import com.web.team.schedule.repository.ScheduleRepository;
import com.web.team.user.domain.Position;
import com.web.team.user.domain.User;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceSuccessTest {

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("관리자 회사 일정 등록 성공")
    void createAdminSchedule_success() {
        // given
        LocalDateTime now = LocalDateTime.of(2025, 7, 17, 10, 0);
        ScheduleCreateRequest request = new ScheduleCreateRequest("회의", "월말 회의", now, now.plusHours(1), ScheduleType.COMPANY);
        Admin admin = mock(Admin.class);

        // when
        scheduleService.createAdminSchedule(request, admin);

        // then
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    @DisplayName("직원 개인 일정 등록 성공")
    void createEmployeeSchedule_success() {
        //given
        LocalDateTime now = LocalDateTime.of(2025, 7, 17, 10, 0);
        ScheduleCreateRequest request = new ScheduleCreateRequest("여행", "가족여행", now, now.plusHours(1), ScheduleType.FAMILY);
        User user = mock(User.class);

        // when
        scheduleService.createEmployeeSchedule(request, user);

        // then
        verify(scheduleRepository).save(any(Schedule.class));
    }

    @Test
    @DisplayName("직원 개인 일정 전체 조회 성공")
    void getScheduleByEmployee_success() {
        // given
        User user = mock(User.class);
        Schedule schedule = mock(Schedule.class);
        when(scheduleRepository.findAllForEmployee(user)).thenReturn(List.of(schedule));

        // when
        List<ScheduleCalendarResponse> result = scheduleService.getSchedulesByEmployee(user);

        // then
        assertEquals(1, result.size());
        verify(scheduleRepository).findAllForEmployee(user);
    }

    @Test
    @DisplayName("관리자 일정 전체 조회 성공")
    void getScheduleByAdmin_success() {
        // given
        Admin admin = mock(Admin.class);

        Schedule schedule1 = Schedule.create("회의1", "회의 내용...",
            LocalDateTime.of(2025,7,20,10,0),
            LocalDateTime.of(2025,7,20,11,0),
            ScheduleType.COMPANY, admin
            );

        Schedule schedule2 = Schedule.create("회의2", "회의 내용...",
            LocalDateTime.of(2025,7,25,15,0),
            LocalDateTime.of(2025,7,25,16,0),
            ScheduleType.COMPANY, admin
            );

        when(scheduleRepository.findAllForAdmin(admin)).thenReturn(List.of(schedule1, schedule2));

        // when
        List<ScheduleCalendarResponse> result = scheduleService.getSchedulesByAdmin(admin);

        // then
        assertEquals(2, result.size());
        assertEquals("회의1", result.get(0).title());
        assertEquals("회의2", result.get(1).title());
    }

    @Test
    @DisplayName("직원 개인 일정 상세 조회 성공")
    void getScheduleForEmployee_success() {
        // given
        User user = mock(User.class);
        Long scheduleId = 1L;

        Schedule schedule = Schedule.create("가족", "가족여행 계획",
            LocalDateTime.of(2025, 8, 20, 9, 0),
            LocalDateTime.of(2025, 8, 20, 10, 0),
            ScheduleType.FAMILY, user
            );
        
        when(scheduleRepository.findByIdForEmployee(scheduleId, user)).thenReturn(Optional.of(schedule));

        // when
        ScheduleResponse result = scheduleService.getScheduleForEmployee(scheduleId, user);

        // then
        assertEquals("가족", result.title());
        assertEquals("가족여행 계획", result.content());
    }

    @Test
    @DisplayName("관리자 회사 일정 상세 조회 성공")
    void getScheduleForAdmin_success() {
        // given
        Admin admin = mock(Admin.class);
        Long scheduleId = 1L;

        Schedule schedule = Schedule.create("중간 회의", "중간 보고서에 대한 브리핑",
            LocalDateTime.of(2025, 8, 22, 9, 0),
            LocalDateTime.of(2025, 8, 22, 10, 0),
            ScheduleType.COMPANY, admin
            );
        
        when(scheduleRepository.findByIdForAdmin(scheduleId, admin)).thenReturn(Optional.of(schedule));

        // when
        ScheduleResponse result = scheduleService.getScheduleForAdmin(scheduleId, admin);

        // then
        assertEquals("중간 회의", result.title());
        assertEquals("중간 보고서에 대한 브리핑", result.content());
    }

    @Test
    @DisplayName("직원 개인 일정 수정 성공")
    void updateEmployeeSchedule_success() {
        // given
        Long scheduleId = 1L;
        Long userId = 100L;

        User user = User.create("user@gmail.com", "password1234", "신짱구", Position.STAFF);
        ReflectionTestUtils.setField(user, "id", userId);

        Schedule schedule = Schedule.create("연차", "연차사용", 
        LocalDateTime.of(2025, 9, 12, 9, 0),
        LocalDateTime.of(2025, 9, 12, 10, 0),
        ScheduleType.PROMISE, user);

        ReflectionTestUtils.setField(schedule, "id", scheduleId);

        ScheduleUpdateRequest request = new ScheduleUpdateRequest("연차 취소", "연차 일정 변경",
        LocalDateTime.of(2025, 9, 12, 9, 0),
        LocalDateTime.of(2025, 9, 12, 10, 0),
        ScheduleType.PROMISE
        );

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // when
        scheduleService.updateEmployeeSchedule(scheduleId, user, request);

        // then
        assertEquals("연차 취소", schedule.getTitle());
        assertEquals("연차 일정 변경", schedule.getContent());
        assertEquals(LocalDateTime.of(2025, 9, 12, 9, 0), schedule.getStartedAt());
        assertEquals(LocalDateTime.of(2025, 9, 12, 10, 0), schedule.getEndAt());
        assertEquals(ScheduleType.PROMISE, schedule.getType());


    }

    @Test
    @DisplayName("관리자 회사 일정 수정 성공")
    void updateAdminSchedule_success() {
        // given
        Long scheduleId = 1L;
        Long adminId = 200L;

        Admin admin = Admin.create("admin@gmail.com", "password1234", "관리자1");
        ReflectionTestUtils.setField(admin, "id", adminId);

        Schedule schedule = Schedule.create("전체 회의",
                                            "임원 모두 참가한다.",
                                            LocalDateTime.of(2025, 9, 15, 14, 0),
                                            LocalDateTime.of(2025, 9, 15, 15, 0),
                                            ScheduleType.COMPANY,
                                            admin
        );

        ReflectionTestUtils.setField(schedule, "id", scheduleId);

        ScheduleUpdateRequest request = new ScheduleUpdateRequest("전체 회의(수정)",
                                                                  "신짱구 임원은 외근으로 인한 불참",
                                                                  LocalDateTime.of(2025, 9, 15, 14, 0),
                                                                  LocalDateTime.of(2025, 9, 15, 15, 0),
                                                                  ScheduleType.COMPANY);
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // when
        scheduleService.updateAdminSchedule(scheduleId, admin, request);

        // then
        assertEquals("전체 회의(수정)", schedule.getTitle());
        assertEquals("신짱구 임원은 외근으로 인한 불참", schedule.getContent());
        assertEquals(LocalDateTime.of(2025, 9, 15, 14, 0), schedule.getStartedAt());
        assertEquals(LocalDateTime.of(2025, 9, 15, 15, 0), schedule.getEndAt());
        assertEquals(ScheduleType.COMPANY, schedule.getType());
    }

    @Test
    @DisplayName("관리자 회사 일정 삭제")
    void deleteAdminSchedule_success() {
        // given
        Long scheduleId = 1L;
        Long adminId = 100L;

        Admin admin = Admin.create("admin@gmail.com", "password1234", "관리자1");
        ReflectionTestUtils.setField(admin, "id", adminId);

        Schedule schedule = Schedule.create("교육 일정",
                                            "신입 사원 교육",
                                            LocalDateTime.of(2025,07,01,14,0),
                                            LocalDateTime.of(2025,07,01,15,0),
                                            ScheduleType.COMPANY,
                                            admin
        );

        ReflectionTestUtils.setField(schedule, "id", scheduleId);

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // when
        scheduleService.deleteAdminSchedule(scheduleId, admin);

        // then
        verify(scheduleRepository).delete(schedule);

    }

    @Test
    @DisplayName("직원 개인 일정 삭제")
    void deleteEmployeeSchedule_success() {
        // given
        Long scheduleId = 1L;
        Long userId = 100L;

        User user = User.create("user1@gmail.com", "password1234", "김철수", Position.DIRECTOR);
        ReflectionTestUtils.setField(user, "id",userId);

        Schedule schedule = Schedule.create("여름 휴가",
                                            "여름 휴가",
                                            LocalDateTime.of(2025,8,01,14,0),
                                            LocalDateTime.of(2025,8,03,15,0),
                                            ScheduleType.PROMISE,
                                            user
        );

        ReflectionTestUtils.setField(schedule, "id", scheduleId);

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // when
        scheduleService.deleteEmployeeSchedule(scheduleId, user);

        // then
        verify(scheduleRepository).delete(schedule);

    }
    
}
