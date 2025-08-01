package com.web.team.board.domain;

import com.web.team.admin.domain.Admin;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String uuid = UUID.randomUUID().toString();

    @Column(unique = true)
    private String title;

    private String content;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin writer;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardImage> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private BoardStatus boardStatus; // PUBLIC , PRIVATE

    public static Board create(String title, String content, Admin writer, List<BoardImage> images) {
        Board board = new Board();
        board.title = title;
        board.content = content;
        board.writer = writer;
        board.createdAt = LocalDateTime.now();
        board.boardStatus = BoardStatus.PUBLIC;
        for (BoardImage image : images) {
            board.addImage(image);
        }

        return board;
    }

    public void addImage(BoardImage image) {
        images.add(image);
        image.setBoard(this);
    }

    public void update(String title, String content) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
    }

    // 파일 새로 수정 시
    public void replaceImages(List<BoardImage> newImages) {
        if (newImages == null || newImages.isEmpty()) return;

        this.images.clear();
        for (BoardImage image : newImages) {
            this.addImage(image);
        }
    }

    // 공개/비공개 상태변경 메서드
    public void changeStatus(BoardStatus status) {
        this.boardStatus = status;
    }

    // 작성한 관리자가 본인인지 확인
    public boolean isWriter(Admin admin) {
        return this.writer != null && this.writer.getId().equals(admin.getId());
    }


}
