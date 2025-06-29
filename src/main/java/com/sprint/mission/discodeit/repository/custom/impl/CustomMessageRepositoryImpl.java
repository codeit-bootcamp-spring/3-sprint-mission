package com.sprint.mission.discodeit.repository.custom.impl;

import static com.sprint.mission.discodeit.entity.QBinaryContent.binaryContent;
import static com.sprint.mission.discodeit.entity.QMessage.message;
import static com.sprint.mission.discodeit.entity.QUser.user;
import static com.sprint.mission.discodeit.entity.QUserStatus.userStatus;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.QMessage;
import com.sprint.mission.discodeit.repository.custom.CustomMessageRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomMessageRepositoryImpl implements CustomMessageRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private static final Logger logger = Logger.getLogger(CustomMessageRepositoryImpl.class.getName()); // 필드로 Logger 선언

    @Override
    public Slice<Message> findAllByChannelIdWithAuthor(UUID channelId, Instant createdAt, Pageable pageable) {

        // QueryDSL 조건 빌더
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(message.channel.id.eq(channelId));

        // createdAt이 null이 아닐 때만 조건 추가
        if (createdAt != null) {
            builder.and(message.createdAt.lt(createdAt));
        } else {
            logger.info("[CustomMessageRepositoryImpl] createdAt이 null이므로 시간 조건 없이 조회");
        }

        JPAQuery<Message> query = jpaQueryFactory
            .selectFrom(message)
            .leftJoin(message.author, user).fetchJoin()
            .join(user.status, userStatus).fetchJoin()
            .leftJoin(user.profile, binaryContent).fetchJoin()
            .leftJoin(message.attachments).fetchJoin()
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

        Instant result = jpaQueryFactory
            .select(message.createdAt)
            .from(message)
            .where(message.channel.id.eq(channelId))
            .orderBy(message.createdAt.desc())
            .limit(1)
            .fetchOne();

        return Optional.ofNullable(result);
    }

}