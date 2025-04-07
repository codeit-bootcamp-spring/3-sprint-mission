package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.Users;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Users users = new Users();


    @Override
    public User createUser(String username) {
        User newUser = new User(username);
        users.add(newUser.getId(), newUser);
        return newUser;
    }

    // 다건
    @Override
    public Map<UUID, User> readUsers() {
        return users.readAll();
    }

    // 단건
    @Override
    public User readUser(UUID id) {
        return users.read(id);
    }

    @Override
    public User updateUser(UUID id, String username) {
        return users.update(id, username);
    }

    @Override
    public User deleteUser(UUID id) {
        return users.remove(id);
    }
}
