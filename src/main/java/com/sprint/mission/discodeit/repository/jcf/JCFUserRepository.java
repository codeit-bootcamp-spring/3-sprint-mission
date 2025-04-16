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
    public void write(User user) {
        this.data.put(user.getId(), user);
    }


    @Override
    public User read(UUID id) {
        return this.data.get(id);
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public boolean delete(UUID id) {
        this.data.remove(id);
        return true;
    }
}
