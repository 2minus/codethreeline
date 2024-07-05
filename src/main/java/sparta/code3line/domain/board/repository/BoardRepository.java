package sparta.code3line.domain.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import sparta.code3line.domain.board.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>,
        QuerydslPredicateExecutor<Board>, BoardRepositoryQuery {

    Optional<Board> findById(Long id);

    List<Board> findAllByUserIdInOrderByCreatedAtDesc(List<Long> followingUserId);

    Page<Board> findAll(Pageable pageable);

    Page<Board> findAllByType(Board.BoardType type, Pageable pageable);

    Page<Board> findAllByTypeNot(Board.BoardType boardType, PageRequest pageRequest);

    Optional<Board> findByType(Board.BoardType type);

}
