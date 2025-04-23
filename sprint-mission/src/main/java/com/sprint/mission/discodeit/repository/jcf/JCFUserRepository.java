package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> users;

    public JCFUserRepository() {
        this.users = new HashMap<>();
    }

    @Override
    public void save(User user){
        users.put(user.getId(),user);
    }

    @Override
    public Map<UUID, User> load() {
        return users;
    }

    @Override
    public void deleteUser(UUID id){
        users.remove(id);
    }
}
