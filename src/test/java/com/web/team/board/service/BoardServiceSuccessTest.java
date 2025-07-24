package com.web.team.board.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.web.team.admin.domain.Admin;
import com.web.team.board.domain.Board;
import com.web.team.board.domain.BoardStatus;
import com.web.team.board.dto.BoardCreateRequest;
import com.web.team.board.dto.BoardDetailResponse;
import com.web.team.board.dto.BoardListResponse;
import com.web.team.board.dto.BoardUpdateRequest;
import com.web.team.board.repository.BoardRepository;

@ExtendWith(MockitoExtension.class)
public class BoardServiceSuccessTest {

    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    @Test
    @DisplayName("공지사항 생성 성공")
    void createBoard_success() {
        // given
        Admin admin = mock(Admin.class);
        String title = "회사 주요 행사";
        String content = "이번 달 마지막 금요일은 오전 근무만 진행합니다.";

        List<MultipartFile> files = new ArrayList<>();

        // 중복 제목이 아닌
        when(boardRepository.existsByTitle(title)).thenReturn(false);

        // 저장
        Board savedBoard = mock(Board.class);
        when(boardRepository.save(any(Board.class))).thenReturn(savedBoard);

        // when
        Board result = boardService.create(new BoardCreateRequest(title, content), files, admin);

        // then
        assertEquals(savedBoard, result);
    }

    @Test
    @DisplayName("공지사항 목록 조회 성공 - 관리자")
    void getAllBoards_success_asAdmin() {
        // given
        Admin admin = Admin.create("admin@gmail.com", "pw", "관리자");
        Board board1 = Board.create("제목 1", "내용 1", admin, List.of());
        Board board2 = Board.create("제목 2", "내용 2", admin, List.of());
        
        ReflectionTestUtils.setField(board1, "uuid", "uuid-1");
        ReflectionTestUtils.setField(board2, "uuid", "uuid-2");

        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(board1, "createdAt", now);
        ReflectionTestUtils.setField(board2, "createdAt", now);

        when(boardRepository.findAll()).thenReturn(List.of(board1, board2));

        // when
        List<BoardListResponse> result = boardService.getAllBoards(true);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("제목 1");
        assertThat(result.get(1).title()).isEqualTo("제목 2");
        
    }

    @Test
    @DisplayName("공지사항 목록 조회 성공 - 직원")
    void getAllBoards_success_asUser() {
        // given
        Admin admin = Admin.create("admin@gmail.com", "pw", "관리자");
        Board board = Board.create("공개 게시글", "내용 1", admin, List.of());
        
        board.changeStatus(BoardStatus.PUBLIC);
        ReflectionTestUtils.setField(board, "uuid", "uuid-public");
        ReflectionTestUtils.setField(board, "createdAt", LocalDateTime.now());

        when(boardRepository.findAllByBoardStatus(BoardStatus.PUBLIC)).thenReturn(List.of(board));

        // when
        List<BoardListResponse> result = boardService.getAllBoards(false);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("공개 게시글");
        assertThat(result.get(0).status()).isEqualTo(BoardStatus.PUBLIC);
    }

    @Test
    @DisplayName("공지사항 상세 조회 성공 - 관리자")
    void getBoard_success_asAdmin() {
        // given
        String uuid = "uuid-123";
        Admin admin = Admin.create("admin@gmail.com", "pw", "관리자1");
        Board board = Board.create("제목1", "내용 1", admin, List.of());

        ReflectionTestUtils.setField(board, "uuid", uuid);
        ReflectionTestUtils.setField(board, "createdAt", LocalDateTime.of(2025, 4, 20, 10 ,0));

        when(boardRepository.findByUuid(uuid)).thenReturn(Optional.of(board));

        // when
        BoardDetailResponse result = boardService.getBoard(uuid, true);

        // then
        assertThat(result.title()).isEqualTo("제목1");
        assertThat(result.writerName()).isEqualTo("관리자1");
        assertThat(result.status()).isEqualTo(BoardStatus.PUBLIC);
    }

    @Test
    @DisplayName("공지사항 상세 조회 성공 - 직원")
    void getBoard_success_asUser_public() {
        // given
        String uuid = "uuid-public";
        Admin admin = Admin.create("admin@gmail.com", "pw", "관리자1");
        Board board = Board.create("제목1", "내용 1", admin, List.of());
        board.changeStatus(BoardStatus.PUBLIC);

        ReflectionTestUtils.setField(board, "uuid", uuid);
        ReflectionTestUtils.setField(board, "createdAt", LocalDateTime.of(2025, 4, 20, 10 ,0));

        when(boardRepository.findByUuid(uuid)).thenReturn(Optional.of(board));

        // when
        BoardDetailResponse result = boardService.getBoard(uuid, false);

        // then
        assertThat(result.title()).isEqualTo("제목1");
        assertThat(result.status()).isEqualTo(BoardStatus.PUBLIC);
    }

    @Test
    @DisplayName("공지사항 수정 성공 - 제목, 내용, 상태 변경")
    void updateBoard_success() {
        // given
        String uuid = "uuid-0001";
        Admin admin = Admin.create("admin@gmai.com", "password1234", "관리자");
        ReflectionTestUtils.setField(admin, "id", 1L);

        Board board = Board.create("제목", "내용", admin, new ArrayList<>());
        ReflectionTestUtils.setField(board, "uuid", uuid);
        ReflectionTestUtils.setField(board, "createdAt", LocalDateTime.now());

        BoardUpdateRequest request = new BoardUpdateRequest(
            "수정된 제목", "수정된 내용", BoardStatus.PUBLIC
        );

        when(boardRepository.findByUuid(uuid)).thenReturn(Optional.of(board));
        
        // when
        boardService.update(uuid, request, null, admin);

        // then
        assertThat(board.getTitle()).isEqualTo("수정된 제목");
        assertThat(board.getContent()).isEqualTo("수정된 내용");
        assertThat(board.getBoardStatus()).isEqualTo(BoardStatus.PUBLIC);
    }

    @Test
    @DisplayName("공지사항 삭제 성공 - 작성자 본인")
    void deleteBoard_success() {
        // given
        String uuid = "uuid-0123";
        Admin admin = Admin.create("admin@gmail.com", "pw", "관리자1");
        ReflectionTestUtils.setField(admin, "id", 1L);

        Board board = Board.create("삭제할 제목", "삭제할 내용", admin, List.of());
        ReflectionTestUtils.setField(board, "uuid", uuid);

        when(boardRepository.findByUuid(uuid)).thenReturn(Optional.of(board));

        // when
        boardService.delete(uuid, admin);

        // then
        verify(boardRepository, times(1)).delete(board);

    }
}
