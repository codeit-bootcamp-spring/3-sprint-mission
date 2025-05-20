package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("jcf")
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
    public User loadByEmail(String email) {
        return null;
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
    public void deleteById(UUID id) {
        if (!users.containsKey(id)) {
            throw new NoSuchElementException("[User] 유효하지 않은 사용자입니다. (" + id + ")");
        }

        users.remove(id);
    }
}
