package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data; //database

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public User write(User user) {
        this.data.put(user.getId(), user);
        return user;
    }

    @Override
    public User read(UUID userId) {
        return this.data.get(userId);
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public void delete(UUID userId) {
        this.data.remove(userId);
    }
}
