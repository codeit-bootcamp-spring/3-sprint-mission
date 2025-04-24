package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
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
@Repository("jcfUserStatusRepository")
public class JcfUserStatusRepository implements UserStatusRepository {
    Map<UUID, UserStatus> data = new HashMap<>();

    // 추가 필요
    @Override
    public Instant onlineTime(UUID userStatusId) {
        return null;
    }

    @Override
    public boolean isOnline(UUID userStatusId) {
        UserStatus userStatus = data.get(userStatusId);
        Instant lastLoginTime = userStatus.getUpdatedAt();
        Duration duration = Duration.between(lastLoginTime, Instant.now());
        return duration.toMinutes() < 5;
    }


    @Override
    public UserStatus createUserStatus(UUID userId) {
        UserStatus userStatus = new UserStatus(userId);
        data.put(userStatus.getId(), userStatus);
        return userStatus;
    }


    @Override
    public UserStatus findUserStatusByUserId(UUID userId) {
        List<UserStatus> allUserStatus = findAllUserStatus();
        for (UserStatus userStatus : allUserStatus) {
            if (userStatus.getUserId().equals(userId)) {
                return userStatus;
            }
        }
        return null;
    }


    @Override
    public List<UserStatus> findAllUserStatus() {
        return data.values().stream().toList();
    }
}
