package com.sprint.mission.discodeit.repository.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.QBinaryContent;
import com.sprint.mission.discodeit.entity.QMessage;
import com.sprint.mission.discodeit.entity.QUser;
import com.sprint.mission.discodeit.entity.QUserStatus;
import com.sprint.mission.discodeit.repository.custom.MessageRepositoryCustom;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Message> findAllByChannelIdWithAuthor(UUID channelId, Instant createdAt,
        Pageable pageable) {
        QMessage message = QMessage.message;
        QUser author = QUser.user;
        QUserStatus userStatus = QUserStatus.userStatus;
        QBinaryContent profile = QBinaryContent.binaryContent;

        JPAQuery<Message> query = queryFactory
            .selectFrom(message)
            .leftJoin(message.author, author).fetchJoin()
            .join(author.status, userStatus).fetchJoin()
            .leftJoin(author.profile, profile).fetchJoin()
            .where(
                message.channel.id.eq(channelId)
                    .and(message.createdAt.lt(createdAt))
            );

        query.orderBy(message.createdAt.desc());

        List<Message> content = query
            .limit(pageable.getPageSize() + 1)
            .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Optional<Instant> findLastMessageAtByChannelId(UUID channelId) {
        QMessage message = QMessage.message;

        Instant result = queryFactory
            .select(message.createdAt)
            .from(message)
            .where(message.channel.id.eq(channelId))
            .orderBy(message.createdAt.desc())
            .limit(1)
            .fetchOne();

        return Optional.ofNullable(result);
    }

}