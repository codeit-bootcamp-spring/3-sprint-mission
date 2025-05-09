package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> data; //database

    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        this.data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        return Optional.ofNullable(this.data.get(userStatusId));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        List<UserStatus> UserStatuses = this.findAll();
        Optional<UserStatus> userStatusNullable = UserStatuses.stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
        return userStatusNullable;
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public boolean existsById(UUID userStatusId) {
        return this.data.containsKey(userStatusId);
    }

    @Override
    public void deleteById(UUID userStatusId) {
        this.data.remove(userStatusId);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        this.findByUserId(userId).ifPresent(userStatus -> this.deleteById(userStatus.getId()));
    }
}
