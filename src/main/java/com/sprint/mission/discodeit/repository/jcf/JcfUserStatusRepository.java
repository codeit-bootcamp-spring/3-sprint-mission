package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.repository.jcf
 * fileName       : JcfUserStatusRepository
 * author         : doungukkim
 * date           : 2025. 4. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 24.        doungukkim       최초 생성
 */
//@Primary
@Repository
public class JcfUserStatusRepository implements UserStatusRepository {
    Map<UUID, UserStatus> data = new HashMap<>();

    // 추가 필요
    @Override
    public Instant onlineTime(UUID userStatusId) {
        return null;
    }
    // 추가 필요
    @Override
    public boolean isOnline(UUID userStatusId) {
        return false;
    }

    @Override
    public UserStatus createUserStatus(UUID userId) {
        UserStatus userStatus = new UserStatus(userId);
        data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

}
