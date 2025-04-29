package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

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

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jcf")
public class JcfUserStatusRepository implements UserStatusRepository {
    Map<UUID, UserStatus> data = new HashMap<>();

    @Override
    public void updateByUserId(UUID userId, Instant newTime) {
        UserStatus userStatus = data.values().stream().filter(us -> us.getUserId().equals(userId))
                .findFirst().orElse(null);
        if (userStatus == null) {
            return;
        }
        userStatus.setUpdatedAt(newTime);
    }

    @Override
    public void update(UUID userStatusId, Instant newTime) {
        if (!data.containsKey(userStatusId)) {
            throw new IllegalStateException("any shit to upload");
        }
        UserStatus userStatus = data.get(userStatusId);
        userStatus.setUpdatedAt(newTime);
    }

    @Override
    public UserStatus findById(UUID userStatusId) {
        return data.get(userStatusId);
    }

    // 추가 필요
    @Override
    public Instant onlineTime(UUID userStatusId) {
        return null;
    }

    @Override
    public boolean isOnline(UUID userStatusId) {
        UserStatus userStatus = Optional.ofNullable(data.get(userStatusId)).orElseThrow(() -> new IllegalStateException("no data to find"));
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

    // 추가 예정

    @Override
    public void deleteById(UUID userStatusId) {
        if (!data.containsKey(userStatusId)) {
            throw new IllegalStateException("no userStatus to delete by eid");
        }
        data.remove(userStatusId);
    }
}
