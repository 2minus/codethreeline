package sparta.code3line.domain.like.repository;

import sparta.code3line.domain.like.entity.LikeBoard;
import sparta.code3line.domain.like.entity.LikeComment;

import java.util.List;

public interface LikeCommentRepositoryQuery {

    List<LikeComment> findFetchCommentsbyUserId(Long userId);

}
