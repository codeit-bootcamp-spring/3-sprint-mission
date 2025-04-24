package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("jcfUserRepository")
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User loadByName(String name) {
        return users.values().stream()
                .filter(u -> u.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User loadById(UUID id) {
        return users.get(id);
    }

    @Override
    public List<User> loadAll() {
        return users.values().stream().toList();
    }

    @Override
    public void update(UUID id, String name) {
        User user = users.get(id);
        if (user == null) {
            throw new IllegalArgumentException("[User] 유효하지 않은 사용자입니다. (" + id + ")");
        }

        user.setName(name);
    }

    @Override
    public void deleteById(UUID id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("[User] 유효하지 않은 사용자입니다. (" + id + ")");
        }

        users.remove(id);
    }
}
