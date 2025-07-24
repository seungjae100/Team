package com.web.team.board.service;

import com.web.team.admin.domain.Admin;
import com.web.team.board.domain.Board;
import com.web.team.board.domain.BoardImage;
import com.web.team.board.domain.BoardStatus;
import com.web.team.board.dto.BoardCreateRequest;
import com.web.team.board.dto.BoardDetailResponse;
import com.web.team.board.dto.BoardListResponse;
import com.web.team.board.dto.BoardUpdateRequest;
import com.web.team.board.repository.BoardRepository;
import com.web.team.exception.CustomException;
import com.web.team.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private BoardRepository boardRepository;

    @Override
    public Board create(BoardCreateRequest request, List<MultipartFile> files, Admin admin) {
        if (boardRepository.existsByTitle(request.getTitle())) {
            throw new CustomException(ErrorCode.DUPLICATE_BOARD_TITLE);
        }

        List<BoardImage> imageEntities = new ArrayList<>();
        if(files != null) {
            for (MultipartFile file : files) {
                validateImage(file);
                BoardImage image = BoardImage.of(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        getBytes(file)
                );
                imageEntities.add(image);
            }
        }

        Board board = Board.create(request.getTitle(), request.getContent(), admin, imageEntities);
        return boardRepository.save(board);
    }

    @Override
    public void update(String uuid, BoardUpdateRequest request, List<MultipartFile> files, Admin admin) {

        Board board = boardRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        // 게시글을 작성한 관리자와 일치하지 않을 때
        if (!board.getWriter().getId().equals(admin.getId())) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        if (request.getTitle() != null && !request.getTitle().equals(board.getTitle()) &&
        boardRepository.existsByTitleAndUuidNot(request.getTitle(), uuid)) {
        throw new CustomException(ErrorCode.DUPLICATE_BOARD_TITLE);
}

        // 제목, 내용 변경 (null 인 필드는 유지)
        board.update(request.getTitle(), request.getContent());

        // 상태 변경 (null이 아닐 때만)
        if (request.getStatus() != null) {
            board.changeStatus(request.getStatus());
        }

        // 새로운 이미지를 등록한 경우에만 수정 , null이면 기존 파일 유지
        if (files != null && !files.isEmpty()) {
            List<BoardImage> newImages = files.stream()
                    .map(file -> BoardImage.of(
                            file.getOriginalFilename(),
                            file.getContentType(),
                            getBytes(file)
                    ))
                    .toList();

            board.replaceImages(newImages);
        }
    }

    @Override
    public List<BoardListResponse> getAllBoards(boolean isAdmin) {
        List<Board> boards = isAdmin
                ? boardRepository.findAll()
                : boardRepository.findAllByBoardStatus(BoardStatus.PUBLIC);

        return boards.stream()
                .map(BoardListResponse::from)
                .toList();
    }

    @Override
    public BoardDetailResponse getBoard(String uuid, boolean isAdmin) {
        Board board = boardRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!isAdmin && board.getBoardStatus() != BoardStatus.PUBLIC) {
            throw new CustomException(ErrorCode.BOARD_PRIVATE);
        }

        return BoardDetailResponse.from(board);
    }

    @Override
    public void delete(String uuid, Admin admin) {
        Board board = boardRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getWriter().getId().equals(admin.getId())) {
            throw new CustomException(ErrorCode.NO_PERMISSION); // 작성자가 삭제 가능
        }

        boardRepository.delete(board);
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/png") || contentType.equals("image/jpeg"))) {
            throw new IllegalArgumentException("JPG 또는 PNG 파일만 허용됩니다.");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("파일은 최대 5MB 까지 업로드 가능합니다.");
        }
    }

    private byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("파일을 읽을 수 없습니다.", e);
        }
    }
    
}
