package sparta.code3line.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import sparta.code3line.domain.like.entity.LikeBoard;

import java.util.List;
import java.util.Optional;

public interface LikeBoardRepository extends JpaRepository<LikeBoard, Long>,
        QuerydslPredicateExecutor<LikeBoard>, LikeBoardRepositoryQuery {

    Optional<LikeBoard> findByUserIdAndBoardId(Long userId, Long commentId);
    List<LikeBoard> findAllbyUserId(Long userId);
}
