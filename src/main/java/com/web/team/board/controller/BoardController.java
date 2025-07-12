package com.web.team.board.controller;

import com.web.team.board.domain.Board;
import com.web.team.board.dto.BoardCreateRequest;
import com.web.team.board.dto.BoardDetailResponse;
import com.web.team.board.dto.BoardListResponse;
import com.web.team.board.dto.BoardUpdateRequest;
import com.web.team.board.service.BoardService;
import com.web.team.exception.CustomException;
import com.web.team.exception.ErrorCode;
import com.web.team.jwt.CustomAdminDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequestMapping("/create")
    public ResponseEntity<?> createBoard(
            @RequestPart("board") @Valid BoardCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
        Board board = boardService.create(request, images, adminDetails.getAdmin());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                       "message", "게시글 등록 완료",
                       "uuid", board.getUuid()
                )
        );
    }

    // 게시글 수정
    @PatchMapping("/{uuid}")
    public ResponseEntity<?> updateBoard(
            @PathVariable String uuid,
            @RequestPart("board")BoardUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
        boardService.update(uuid, request, images, adminDetails.getAdmin());
        return ResponseEntity.ok("게시글 수정 완료");
    }

    // 모든 게시글 목록 조회
    @GetMapping
    public ResponseEntity<List<BoardListResponse>> getAllBoards(@AuthenticationPrincipal Object userDetails) {
        if (userDetails == null) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED);
        }

        boolean isAdmin = userDetails instanceof  CustomAdminDetails;
        List<BoardListResponse> result = boardService.getAllBoards(isAdmin);
        return ResponseEntity.ok(result);
    }

    // 게시글 세부내용 조회
    @GetMapping("/{uuid}")
    public ResponseEntity<BoardDetailResponse> getBoard(
            @PathVariable String uuid,
            @AuthenticationPrincipal Object userDetails
    ) {
        if (userDetails == null) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED);
        }

        boolean isAdmin = userDetails instanceof  CustomAdminDetails;
        BoardDetailResponse response = boardService.getBoard(uuid, isAdmin);
        return ResponseEntity.ok(response);
    }

    // 게시글 삭제
    @DeleteMapping("{/uuid}")
    public ResponseEntity<?> deleteBoard(
            @PathVariable String uuid,
            @AuthenticationPrincipal CustomAdminDetails adminDetails
    ) {
        boardService.delete(uuid, adminDetails.getAdmin());
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }


}
