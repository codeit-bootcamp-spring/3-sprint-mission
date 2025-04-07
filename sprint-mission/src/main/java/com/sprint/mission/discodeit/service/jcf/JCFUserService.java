package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User createUser(String username) {
        User user = new User(username);
        data.put(user.getId(), user);
        return user;
    }

    // 다건
    @Override
    public Map<UUID, User> readUsers() {
        return data;
    }

    // 단건
    @Override
    public User readUser(UUID id) {
        return data.get(id);
    }

    @Override
    public User updateUser(UUID id, String username) {
        User user = data.get(id);
        user.updateUserName(username);
        return user;
    }

    @Override
    public User deleteUser(UUID id) {
        return data.remove(id);
    }
}
