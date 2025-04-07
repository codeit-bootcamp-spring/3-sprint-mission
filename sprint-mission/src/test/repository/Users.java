package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Users {
    private final Map<UUID, User> users = new HashMap<>();

    public User add(UUID id, User user) {
        users.put(id, user);
        return user;
    }

    public User read(UUID id) {
        return users.get(id);
    }

    public Map<UUID, User> readAll() {
        return users;
    }

    public User remove(UUID id) {
        return users.remove(id);
    }


    public User update(UUID id, String userName) {
        User user = users.get(id);
        user.updateUserName(userName);
        return user;
    }

}
