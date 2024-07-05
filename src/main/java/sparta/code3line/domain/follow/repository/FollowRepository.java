package sparta.code3line.domain.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import sparta.code3line.domain.follow.entity.Follow;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long>,
QuerydslPredicateExecutor<Follow>, FollowRepositoryQuery {

    Optional<Follow> findByFollowingIdAndFollowerId(Long followingId, Long id);

    List<Follow> findAllByFollowerId(Long followingId);

}
