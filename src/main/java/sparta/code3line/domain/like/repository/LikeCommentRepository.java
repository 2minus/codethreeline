package sparta.code3line.domain.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import sparta.code3line.domain.like.entity.LikeBoard;
import sparta.code3line.domain.like.entity.LikeComment;

import java.util.List;
import java.util.Optional;

public interface LikeCommentRepository extends JpaRepository<LikeComment, Long>,
        QuerydslPredicateExecutor<LikeBoard>, LikeCommentRepositoryQuery {

    Optional<LikeComment> findByUserIdAndCommentId(Long userId, Long commentId);

    List<LikeComment> findAllbyUserId(Long id);
}
