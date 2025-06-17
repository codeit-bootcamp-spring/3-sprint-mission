package com.sprint.mission.discodeit.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.QBinaryContent;
import com.sprint.mission.discodeit.entity.QReadStatus;
import com.sprint.mission.discodeit.entity.QUser;
import com.sprint.mission.discodeit.entity.QUserStatus;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.custom.ReadStatusRepositoryCustom;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReadStatusRepositoryImpl implements ReadStatusRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReadStatus> findAllByChannelIdWithUser(UUID channelId) {
        QReadStatus readStatus = QReadStatus.readStatus;
        QUser user = QUser.user;
        QUserStatus status = QUserStatus.userStatus;
        QBinaryContent profile = QBinaryContent.binaryContent;

        return queryFactory
            .selectFrom(readStatus)
            .join(readStatus.user, user).fetchJoin()
            .join(user.status, status).fetchJoin()
            .leftJoin(user.profile, profile).fetchJoin()
            .where(readStatus.channel.id.eq(channelId))
            .fetch();
    }
}
