package com.sprint.mission.discodeit.repository.custom.impl;

import static com.sprint.mission.discodeit.entity.QBinaryContent.binaryContent;
import static com.sprint.mission.discodeit.entity.QUser.user;
import static com.sprint.mission.discodeit.entity.QUserStatus.userStatus;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.custom.CustomUserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<User> findAllWithProfileAndStatus() {
        return jpaQueryFactory
            .selectFrom(user).distinct()
            .leftJoin(user.profile, binaryContent).fetchJoin()
            .join(user.status, userStatus).fetchJoin()
            .fetch();
    }
}
