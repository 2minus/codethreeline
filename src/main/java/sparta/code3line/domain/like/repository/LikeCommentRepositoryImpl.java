package sparta.code3line.domain.like.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import sparta.code3line.common.exception.CustomException;
import sparta.code3line.common.exception.ErrorCode;
import sparta.code3line.domain.like.entity.LikeComment;
import sparta.code3line.domain.user.entity.User;
import sparta.code3line.domain.user.repository.UserRepository;

import java.util.List;

import static sparta.code3line.domain.like.entity.QLikeComment.likeComment;

@RequiredArgsConstructor
public class LikeCommentRepositoryImpl implements LikeCommentRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepository userRepository;

    @Override
    public List<LikeComment> findFetchCommentsbyUserId(Long userId) {
        User user = userRepository.findUserByUserId(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_DIFFERENT)
        );

        return jpaQueryFactory.select(likeComment)
                .from(likeComment)
                .where(likeComment.user.eq(user))
                .leftJoin(likeComment.comment).fetchJoin()
                .fetch();
    }
}
