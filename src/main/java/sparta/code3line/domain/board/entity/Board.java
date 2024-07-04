package sparta.code3line.domain.board.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.code3line.common.Timestamp;
import sparta.code3line.domain.comment.entity.Comment;
import sparta.code3line.domain.like.entity.LikeBoard;
import sparta.code3line.domain.like.entity.LikeComment;
import sparta.code3line.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Board extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    private int likeCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType type;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardFiles> boardFiles = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeBoard> likes = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Board(User user, String title, String contents, BoardType type) {

        this.user = user;
        this.title = title;
        this.contents = contents;
        this.type = type;

    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContents(String contents) {
        this.contents = contents;
    }

    public void updateType(BoardType type) {
        this.type = type;
    }

    public void updateLikesCount() {
        this.likeCount = this.likes.size();
    }

    public enum BoardType {
        // 일반 게시물
        NORMAL,
        // 공지 게시물
        NOTICE,
        // 최상단 고정 게시물
        PICK
    }

}
