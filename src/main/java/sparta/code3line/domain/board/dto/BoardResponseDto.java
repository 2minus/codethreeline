package sparta.code3line.domain.board.dto;

import lombok.Data;
import sparta.code3line.domain.board.entity.Board;

import java.time.LocalDateTime;

@Data
public class BoardResponseDto {

    private String nickname;
    private Long boardId;
    private String title;
    private String contents;
    private int likeCount;

    public BoardResponseDto(Board board) {

        this.nickname = board.getUser().getNickname();
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.likeCount = board.getLikeCount();

    }

}
