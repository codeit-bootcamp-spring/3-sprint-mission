package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User createUser(String name) {
        User user = new User(name);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return data.values().stream().toList();
    }

    @Override
    public void updateUser(UUID id, String name) {
        User user = data.get(id);
        if (user != null) {
            user.update(name);
        }
    }

    @Override
    public void deleteUser(UUID id) {
        data.remove(id);
    }
}
