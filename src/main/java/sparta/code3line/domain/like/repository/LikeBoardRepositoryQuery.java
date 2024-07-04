package sparta.code3line.domain.like.repository;

import sparta.code3line.domain.board.entity.Board;
import sparta.code3line.domain.like.entity.LikeBoard;

import java.util.List;

public interface LikeBoardRepositoryQuery {

    List<LikeBoard> getLikeBoardsbyUserId(Long userId, long offset, int pagesize);

    Long getLikeBoardCount(Long userId);
}
