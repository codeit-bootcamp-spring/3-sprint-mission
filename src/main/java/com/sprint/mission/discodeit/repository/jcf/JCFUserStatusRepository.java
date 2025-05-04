package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.*;

public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> storage = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        storage.put(userStatus.getUserId(), userStatus);
        return userStatus;
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        return storage.get(userId);
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteByUserId(UUID userId) {
        storage.remove(userId);
    }
}