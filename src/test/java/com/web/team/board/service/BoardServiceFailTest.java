package com.web.team.board.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.web.team.admin.domain.Admin;
import com.web.team.board.domain.Board;
import com.web.team.board.domain.BoardStatus;
import com.web.team.board.dto.BoardCreateRequest;
import com.web.team.board.dto.BoardUpdateRequest;
import com.web.team.board.repository.BoardRepository;
import com.web.team.exception.CustomException;
import com.web.team.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class BoardServiceFailTest {

    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    @Test
    @DisplayName("공지사항 생성 실패 - 중복된 제목")
    void createBoard_fail_duplicateTitile() {
        // given
        Admin admin = Admin.create("admin@gmail.com", "pw", "관리자1");

        String title = "중복된 제목";
        String content = "내용";

        when(boardRepository.existsByTitle(title)).thenReturn(true); // 중복된 제목설정
        
        // when & then
        CustomException ex = assertThrows(CustomException.class, () ->
            boardService.create(new BoardCreateRequest(title, content), null, admin)
        );
        
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_BOARD_TITLE);

    }
    
    @Test
    @DisplayName("공지사항 목록 조회 실패 - 관리자 DB 오류")
    void getAllBoards_fail_asAdmin_DBError() {
        // given
        when(boardRepository.findAll()).thenThrow(new RuntimeException("DB 에러"));

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class,  () ->
            boardService.getAllBoards(true)
        );

        assertThat(ex.getMessage()).isEqualTo("DB 에러");
    }

    @Test
    @DisplayName("공지사항 목록 조회 실패 - 사용자 DB 오류")
    void getAllBoards_fail_asUser_DBError() {
        // given
        when(boardRepository.findAllByBoardStatus(BoardStatus.PUBLIC))
        .thenThrow(new RuntimeException("DB 오류"));

        // when & then
        RuntimeException ex = assertThrows(RuntimeException.class,  () ->
            boardService.getAllBoards(false)
        );

        assertThat(ex.getMessage()).isEqualTo("DB 오류");
    }

    @Test
    @DisplayName("공지사항 상세 조회 실패 - 게시글 없음")
    void getBaord_fail_notFound() {
        // given
        String uuid = "ffejklags";
        when(boardRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        // when & then
        CustomException ex = assertThrows(CustomException.class, () ->
            boardService.getBoard(uuid, true)
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BOARD_NOT_FOUND);
    }

    @Test
    @DisplayName("공지사항 상세 조회 실패 - 비공개글 일반 직원 조회")
    void getBoard_fail_private_ForUser() {
        // given
        String uuid = "private-uuid";

        Admin admin = Admin.create("admin@gmail.com", "password", "관리자");
        Board board = Board.create("비공개제목", "내용", admin, List.of());
        board.changeStatus(BoardStatus.PRIVATE);

        when(boardRepository.findByUuid(uuid)).thenReturn(Optional.of(board));

        // when & then
        CustomException ex = assertThrows(CustomException.class, () -> 
            boardService.getBoard(uuid, false)
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BOARD_PRIVATE);
    }
    
    @Test
    @DisplayName("공지사항 수정 실패 - 게시글이 존재하지 않음")
    void updateBoard_fail_notFound() {
        // given
        Admin admin = Admin.create("admin@gmail.com", "password", "관리자");
        String uuid = "non-uuid";

        BoardUpdateRequest request = new BoardUpdateRequest(
            "수정 제목",
            "수정 내용",
            BoardStatus.PUBLIC
        );
        
        when(boardRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        // when & then
        CustomException ex = assertThrows(CustomException.class, () ->
            boardService.update(uuid, request, null, admin)
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BOARD_NOT_FOUND);

    }
    
    @Test
    @DisplayName("공지사항 수정 실패 - 작성자가 아님")
    void updateBoard_fail_notAuthor() {
        // given
        Admin writer = Admin.create("admin@gmail.com", "passwored", "관리자1");
        Admin writer2 = Admin.create("admin2@gmail.com", "passwored", "관리자2");

        ReflectionTestUtils.setField(writer, "id", 1L);
        ReflectionTestUtils.setField(writer2, "id", 2L);

        Board board = Board.create("제목", "수정", writer, List.of());
        ReflectionTestUtils.setField(board, "uuid", "uuid-2");

        

        when(boardRepository.findByUuid("uuid-2")).thenReturn(Optional.of(board));

        // when & then
        CustomException ex = assertThrows(CustomException.class, () ->
            boardService.update("uuid-2", null, null, writer2)
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NO_PERMISSION);
    }

    @Test
    @DisplayName("공지사항 수정 실패 - 다른 글에서 사용 중인 제목으로 수정")
    void updateBoard_fail_duplicateTitleFromAnotherPost() {
        // given
        Admin admin = Admin.create("admin@gmail.com", "password", "관리자1");
        ReflectionTestUtils.setField(admin, "id", 1L);

        Board board = Board.create("제목", "내용", admin, List.of());
        ReflectionTestUtils.setField(board, "uuid", "uuid-1");

        String duplicateTitle = "이미 다른 공지사항 제목";

        when(boardRepository.findByUuid("uuid-1")).thenReturn(Optional.of(board));
        when(boardRepository.existsByTitleAndUuidNot(duplicateTitle, "uuid-1")).thenReturn(true);

        BoardUpdateRequest request = new BoardUpdateRequest(duplicateTitle, null, null);

        // when & then
        CustomException ex = assertThrows(CustomException.class, () ->
            boardService.update("uuid-1", request, List.of(), admin)
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.DUPLICATE_BOARD_TITLE);
    }

    @Test
    @DisplayName("공지사항 삭제 실패 - 작성자가 아닌 관리자가 삭제 요청")
    void deleteBoard_fail_noPermission() {
        // given
        Admin writer = Admin.create("writer@admin.com", "password", "작성자");
        ReflectionTestUtils.setField(writer, "id", 1L);

        Admin otherAdmin = Admin.create("other@admin.com", "password", "다른 관리자");
        ReflectionTestUtils.setField(otherAdmin, "id", 2L);

        Board board = Board.create("제목", "내용", writer, List.of());
        ReflectionTestUtils.setField(board, "uuid", "uuid-1");

        when(boardRepository.findByUuid("uuid-1")).thenReturn(Optional.of(board));

        // when & then
        CustomException ex = assertThrows(CustomException.class, () ->
                boardService.delete("uuid-1", otherAdmin)
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NO_PERMISSION);

    }

    @Test
    @DisplayName("공지사항 삭제 실패 - 존재하지 않는 공지사항 UUID")
    void deleteBoard_fail_notFound() {
        // given
        String uuid = "fneow-uuid";
        Admin admin = Admin.create("admin@gmail.com", "password", "관리자 1");

        when(boardRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        // when & then
        CustomException ex = assertThrows(CustomException.class, () ->
            boardService.delete(uuid, admin)
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.BOARD_NOT_FOUND);


    }
}
