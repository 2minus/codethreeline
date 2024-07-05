package sparta.code3line.domain.board.repository;

import sparta.code3line.domain.board.entity.Board;

import java.util.List;

public interface BoardRepositoryQuery {
    List<Board> getfollowingBoard(Long userId, long offset);
    List<Board> getfollowingBoardOrderByName(Long userId, long offset);
}
