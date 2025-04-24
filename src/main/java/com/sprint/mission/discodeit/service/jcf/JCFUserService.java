package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    // 저장 로직
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User createUser(User user) {
        // 저장 로직
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUser(UUID userId) {
        // 저장 로직
        return Optional.ofNullable(data.get(userId));
    }

    @Override
    public List<User> getAllUsers() {
        // 저장 로직
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateUser(User user) {
        // 저장 로직
        data.put(user.getId(), user);
    }

    @Override
    public void deleteUser(UUID userId) {
        // 저장 로직
        data.remove(userId);
    }
}