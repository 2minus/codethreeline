package sparta.code3line.domain.comment.dto;

import lombok.Builder;
import lombok.Data;
import sparta.code3line.domain.comment.entity.Comment;

@Data
public class CommentResponseDto {

    private Long userId;
    private Long boardId;
    private String contents;
    private int likeCount;

    public CommentResponseDto(Comment comment) {
        this.userId = comment.getUser().getId();
        this.boardId = comment.getBoard().getId();
        this.contents = comment.getContents();
        this.likeCount = comment.getLikeCount();
    }

}
