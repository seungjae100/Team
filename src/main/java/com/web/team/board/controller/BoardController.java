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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "회사 공지사항 게시판 API", description = "회사 공지사항 관련 API 입니다.")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "관리자 공지사항 등록", description = "관리자가 회사 공지사항을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 등록 성공"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
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

    @Operation(summary = "공지사항 수정", description = "관리자가 공지사항을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 수정을 완료하였습니다."),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError"),
    })
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

    @Operation(summary = "모든 공지사항 조회", description = "관리자가 등록한 공지사항을 전체 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 공지사항 조회"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
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

    @Operation(summary = "공지사항 세부내용 조회", description = "공지사항의 단 건에 대한 세부 내용을 확인할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공지사항 세부내용 조회 성공"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
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

    @Operation(summary = "공지사항 게시글 삭제", description = "공지사항 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "공지사항 삭제 성공"),
            @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
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
