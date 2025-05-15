package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserStatusRepository implements UserStatusRepository {

    private static volatile JCFUserStatusRepository instance;
    private final Map<UUID, UserStatus> userStatuses = new ConcurrentHashMap<>();

    private JCFUserStatusRepository() {}

    public static JCFUserStatusRepository getInstance() {
        JCFUserStatusRepository result = instance;
        if (result == null) {
            synchronized (JCFUserStatusRepository.class) {
                result = instance;
                if (result == null) {
                    instance = result = new JCFUserStatusRepository();
                }
            }
        }
        return result;
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        userStatuses.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(userStatuses.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatuses.values().stream()
                .filter(status -> status.getUserId().equals(userId)) // Assuming UserStatus entity has getUserId
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(userStatuses.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return userStatuses.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        userStatuses.remove(id);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        userStatuses.values().removeIf(status -> status.getUserId().equals(userId));
    }
} 