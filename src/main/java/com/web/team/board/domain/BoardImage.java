package com.web.team.board.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardImage {

    @Id
    @GeneratedValue
    private Long id;

    private String originalFilename;

    private String contentType;

    @Lob
    @Column(columnDefinition = "LONGBLOB", nullable = false)
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public void setBoard(Board board) {
        this.board = board;
    }

    public static BoardImage of(String originalFilename, String contentType, byte[] data) {
        BoardImage image = new BoardImage();
        image.originalFilename = originalFilename;
        image.contentType = contentType;
        image.data = data;
        return image;
    }
}
