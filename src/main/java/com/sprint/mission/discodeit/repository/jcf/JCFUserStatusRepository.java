package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entitiy.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "JCF",matchIfMissing = true)
public class JCFUserStatusRepository implements UserStatusRepository {

    private final CopyOnWriteArrayList<UserStatus> data = new CopyOnWriteArrayList<>();
    
    @Override
    public UserStatus save(UserStatus userStatus) {
        data.add(userStatus);
        return userStatus;
    }

    @Override
    public List<UserStatus> read() {
        return data;
    }

    @Override
    public Optional<UserStatus> readById(UUID id) {
        Optional<UserStatus> userStatus = data.stream()
                .filter((u)->u.getId().equals(id))
                .findAny();
        return userStatus;
    }

    @Override
    public Optional<UserStatus> readByUserId(UUID userId) {
        Optional<UserStatus> userStatus = data.stream()
                .filter((u)->u.getUserId().equals(userId))
                .findAny();
        return userStatus;
    }

    @Override
    public void update(UUID id, UserStatus userStatus) {
        data.stream()
                .filter((c)->c.getId().equals(id))
                .forEach((c)->{
                    c.setUpdatedAt(Instant.now());
                    c.setUserId(userStatus.getUserId());
                });
    }

    @Override
    public void delete(UUID userStatusId) {
        data.removeIf(userStatus -> userStatus.getId().equals(userStatusId));
    }
}
