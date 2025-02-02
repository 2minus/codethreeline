package sparta.code3line.domain.like.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sparta.code3line.common.exception.CustomException;
import sparta.code3line.common.exception.ErrorCode;
import sparta.code3line.domain.like.entity.LikeComment;
import sparta.code3line.domain.user.entity.User;
import sparta.code3line.domain.user.repository.UserRepository;

import java.util.List;

import static sparta.code3line.domain.like.entity.QLikeComment.likeComment;

@Repository
@RequiredArgsConstructor
public class LikeCommentRepositoryImpl implements LikeCommentRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepository userRepository;

    @Override
    public List<LikeComment> getLikeCommentsbyUserId(Long userId, long offset, int pagesize) {

        User user = userRepository.findUserById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_DIFFERENT)
        );

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, likeComment.comment.createdAt);

        return jpaQueryFactory.select(likeComment)
                .from(likeComment)
                .where(likeComment.user.eq(user))
                .leftJoin(likeComment.comment).fetchJoin()
                .offset(offset)
                .limit(pagesize)
                .orderBy(orderSpecifier)
                .fetch();
    }


    @Override
    public Long getLikeCommentCount(Long userId) {

        return jpaQueryFactory.select(likeComment.count())
                .from(likeComment)
                .where(likeComment.user.id.eq(userId))
                .fetchOne();
    }
}
