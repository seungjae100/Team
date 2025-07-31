package com.web.team.board.service;

import com.web.team.admin.domain.Admin;
import com.web.team.board.domain.Board;
import com.web.team.board.dto.BoardCreateRequest;
import com.web.team.board.dto.BoardDetailResponse;
import com.web.team.board.dto.BoardListResponse;
import com.web.team.board.dto.BoardUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {

    // 공지사항 등록 메서드
    Board create(BoardCreateRequest request, Admin admin);

    // 공지사항 이미지 업로드 메서드
    void uploadImages(String uuid, List<MultipartFile> images, Admin admin);

    // 공지사항 수정 메서드
    void update(String uuid, BoardUpdateRequest request, List<MultipartFile> images, Admin admin);

    // 공지사항 목록 조회 메서드
    List<BoardListResponse> getAllBoards(boolean isAdmin);

    // 공지사항 목록 세부내용 조회 메서드
    BoardDetailResponse getBoard(String uuid, boolean isAdmin);

    // 게시글 삭제 메서드
    void delete(String uuid, Admin admin);


}
