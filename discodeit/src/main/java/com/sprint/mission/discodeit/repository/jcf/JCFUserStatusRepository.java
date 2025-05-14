package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;


@ConditionalOnProperty(name = "discodeit.repository", havingValue = "jcf")
@Repository
public class JCFUserStatusRepository implements UserStatusRepository {
        private final Map<UUID, UserStatus> userStatusData;

        public JCFUserStatusRepository() {
            this.userStatusData = new HashMap<>();
        }

    @Override
    public UserStatus save(UserStatus userStatus) {
        this.userStatusData.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(this.userStatusData.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return this.userStatusData.values()
                .stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return this.userStatusData.values().stream().toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return this.userStatusData.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        this.userStatusData.remove(id);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        userStatusData.values().removeIf(userStatus -> userStatus.getUserId().equals(userId));
    }
}
