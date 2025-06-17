package com.sprint.mission.discodeit.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.QBinaryContent;
import com.sprint.mission.discodeit.entity.QUser;
import com.sprint.mission.discodeit.entity.QUserStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.custom.UserRepositoryCustom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> findAllWithProfileAndStatus() {
        QUser user = QUser.user;
        QUserStatus status = QUserStatus.userStatus;
        QBinaryContent profile = QBinaryContent.binaryContent;

        return queryFactory
            .selectFrom(user).distinct()
            .leftJoin(user.profile, profile).fetchJoin()
            .join(user.status, status).fetchJoin()
            .fetch();
    }

}
