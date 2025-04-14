package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public void save(User user){
        users.put(user.getId(),user);
    }

    @Override
    public Map<UUID, User> readUsers() {
        return users;
    }

    @Override
    public User readUser(UUID id){
        return users.get(id);
    }

    @Override
    public void deleteUser(UUID id){
        users.remove(id);
    }
}
