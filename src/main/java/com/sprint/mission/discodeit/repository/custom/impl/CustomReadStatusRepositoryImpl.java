package com.sprint.mission.discodeit.repository.custom.impl;

import static com.sprint.mission.discodeit.entity.QBinaryContent.binaryContent;
import static com.sprint.mission.discodeit.entity.QReadStatus.readStatus;
import static com.sprint.mission.discodeit.entity.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.custom.CustomReadStatusRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomReadStatusRepositoryImpl implements CustomReadStatusRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ReadStatus> findAllByChannelIdWithUser(UUID channelId) {
        return jpaQueryFactory
            .selectFrom(readStatus)
            .join(readStatus.user, user).fetchJoin()
            .leftJoin(user.profile, binaryContent).fetchJoin()
            .where(readStatus.channel.id.eq(channelId))
            .fetch();
    }
}
