package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")

public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> userStatuses;

    public JCFUserStatusRepository() {
        this.userStatuses = new HashMap<>();
    }

    @Override
    public UserStatus createUserStatus(UserStatus userStatus) {
        userStatuses.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findStatusById(UUID statusId) {
        return Optional.ofNullable(userStatuses.get(statusId));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatuses.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAllStatus() {
        return userStatuses.values().stream().toList();
    }

    @Override
    public boolean updateUserStatus(UserStatus userStatus) {
        userStatuses.put(userStatus.getId(), userStatus);
        return true;
    }

    @Override
    public boolean updateByUserId(UserStatus userStatus) {
        userStatuses.put(userStatus.getId(), userStatus);
        return true;
    }

    @Override
    public boolean deleteUserStatus(UUID statusId) {
        userStatuses.remove(statusId);
        return true;
    }
}
