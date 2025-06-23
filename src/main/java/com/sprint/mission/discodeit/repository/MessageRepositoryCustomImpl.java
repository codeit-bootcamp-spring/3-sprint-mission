package com.sprint.mission.discodeit.repository;

import com.querydsl.core.BooleanBuilder;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.QMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * PackageName  : com.sprint.mission.discodeit.repository.jpa
 * FileName     : MessageRepositoryImpl
 * Author       : dounguk
 * Date         : 2025. 6. 23.
 */
@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom {

    private final JPAQueryFactory query;
    private static final QMessage m = QMessage.message;

    @Override
    public List<Message> findSliceByCursor(UUID channelId, Instant cursor, Pageable pageable) {

        QMessage m = QMessage.message;
        BooleanBuilder where = new BooleanBuilder()
            .and(m.channel.id.eq(channelId));

        if (cursor != null) {
            where.and(m.createdAt.lt(cursor));
        }

        return query.selectFrom(m)
            .where(where)
            .orderBy(m.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();
    }
}


