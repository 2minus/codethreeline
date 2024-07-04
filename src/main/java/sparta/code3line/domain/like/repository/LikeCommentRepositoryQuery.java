package sparta.code3line.domain.like.repository;

import sparta.code3line.domain.like.entity.LikeComment;

import java.util.List;

public interface LikeCommentRepositoryQuery {

    List<LikeComment> getLikeCommentsbyUserId(Long userId, long offset, int pagesize);

}
