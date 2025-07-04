package com.web.team.board.repository;

import com.web.team.board.domain.Board;
import com.web.team.board.domain.BoardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 중복된 게시판 제목 불가한 메서드
    boolean existsByTitle(String title);

    // 내부값 보안
    Optional<Board> findByUuid(String uuid);

    // 모든 게시판의 상태를 구분
    List<Board> findAllByBoardStatus(BoardStatus status);

}
