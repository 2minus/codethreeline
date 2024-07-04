package sparta.code3line.domain.like.repository;

import sparta.code3line.domain.like.entity.LikeBoard;

import java.util.List;

public interface LikeBoardRepositoryQuery {

    List<LikeBoard> findFetchBoardsbyUserId(Long userId);

}
