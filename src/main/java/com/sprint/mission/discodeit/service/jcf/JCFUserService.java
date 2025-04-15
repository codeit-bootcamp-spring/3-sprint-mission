package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public User createUser(String name) {
        // 이름 중복 검사
        if (getUserByName(name) != null) {
            throw new IllegalArgumentException("[User] 이미 존재하는 사용자 이름입니다. (" + name + ")");
        }

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
    public User getUserByName(String name) {
        return data.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void updateUser(UUID id, String name) {
        User user = data.get(id);
        if (user != null) {
            user.setName(name);
        }
    }

    @Override
    public void deleteUser(UUID id) {
        data.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }
}
