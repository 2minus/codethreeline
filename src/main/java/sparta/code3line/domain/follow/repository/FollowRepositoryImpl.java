package sparta.code3line.domain.follow.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sparta.code3line.domain.follow.dto.QTopFollowerResponseDto;
import sparta.code3line.domain.follow.dto.TopFollowerResponseDto;

import java.util.List;

import static sparta.code3line.domain.follow.entity.QFollow.follow;
import static sparta.code3line.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepositoryQuery{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TopFollowerResponseDto> getTopFollower() {

        OrderSpecifier<?> orderSpecifier = new OrderSpecifier<>(Order.DESC, follow.count());

        return jpaQueryFactory.select(new QTopFollowerResponseDto(
                        user.id,
                        follow.count().intValue()
                ))
                .from(user)
                .leftJoin(follow).on(follow.following.eq(user)).fetchJoin()
                .groupBy(user.id)
                .orderBy(orderSpecifier)
                .limit(10)
                .fetch();
    }

}
