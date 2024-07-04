package sparta.code3line.domain.like.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import sparta.code3line.domain.like.entity.LikeBoard;
import sparta.code3line.domain.user.repository.UserRepository;

import java.util.List;

import static sparta.code3line.domain.like.entity.QLikeBoard.likeBoard;

@RequiredArgsConstructor
public class LikeBoardRepositoryImpl implements LikeBoardRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepository userRepository;

    @Override
    public List<LikeBoard> getLikeBoardsbyUserId(Long userId, long offset, int pagesize) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, likeBoard.board.createdAt);

        return jpaQueryFactory.selectFrom(likeBoard)
                .where(likeBoard.user.id.eq(userId))
                .leftJoin(likeBoard.board).fetchJoin()
                .offset(offset)
                .limit(pagesize)
                .orderBy(orderSpecifier)
                .fetch();
    }

    @Override
    public Long getLikeBoardCount(Long userId) {
        return jpaQueryFactory.select(likeBoard.count())
                .from(likeBoard)
                .where(likeBoard.user.id.eq(userId))
                .fetchOne();
    }
}
