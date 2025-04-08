package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User createUser(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(UUID userId) {
        return data.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateUser(UUID userId, String userName) {
        if (data.containsKey(userId)) {
            data.get(userId).updateUserName(userName);
        }
    }

    @Override
    public void deleteUser(UUID userId) {
        data.remove(userId);
    }
}
