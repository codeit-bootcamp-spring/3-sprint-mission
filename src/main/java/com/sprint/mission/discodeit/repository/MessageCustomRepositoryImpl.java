package com.sprint.mission.discodeit.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.QMessage;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MessageCustomRepositoryImpl implements MessageCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Message> findByChannelIdWithCursor(UUID channelId,
        Instant cursor,
        Sort.Direction direction,
        int limit
    ) {
        QMessage message = QMessage.message;

        BooleanBuilder where = new BooleanBuilder()
            .and(message.channel.id.eq(channelId));

        if (cursor != null) {
            if (direction == Sort.Direction.DESC) {
                where.and(message.createdAt.lt(cursor));
            } else {
                where.and(message.createdAt.gt(cursor));
            }
        }

        OrderSpecifier<?>[] orders = direction == Sort.Direction.DESC
            ? new OrderSpecifier[]{message.createdAt.desc(), message.id.desc()}
            : new OrderSpecifier[]{message.createdAt.asc(), message.id.asc()};

        return queryFactory
            .selectFrom(message)
            .leftJoin(message.author).fetchJoin()
            .where(where)
            .orderBy(orders)
            .limit(limit)
            .fetch();
    }
}
