package sparta.code3line.domain.board.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import sparta.code3line.domain.board.entity.Board;

import java.util.List;

import static sparta.code3line.domain.board.entity.QBoard.board;
import static sparta.code3line.domain.follow.entity.QFollow.follow;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Board> getfollowingBoard(Long userId, long offset) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, board.createdAt);

        return jpaQueryFactory.select(board)
                .from(board)
                .where(
                        JPAExpressions.select(follow)
                                .from(follow)
                                .where(follow.follower.id.eq(userId)
                                        .and(follow.following.id.eq(board.user.id)))
                                .exists()
                )
                .groupBy(board.id)
                .offset(offset)
                .orderBy(orderSpecifier)
                .limit(5)
                .fetch();

    }

    @Override
    public List<Board> getfollowingBoardOrderByName(Long userId, long offset) {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, board.user.nickname);

        return jpaQueryFactory.select(board)
                .from(board)
                .where(
                        JPAExpressions.select(follow)
                                .from(follow)
                                .where(follow.follower.id.eq(userId)
                                                .and (follow.following.id.eq(board.user.id)))
                                .exists()
                )
                .groupBy(board.id)
                .offset(offset)
                .orderBy(orderSpecifier)
                .limit(5)
                .fetch();
    }
}
