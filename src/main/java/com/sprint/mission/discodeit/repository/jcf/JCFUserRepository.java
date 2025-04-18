package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);

        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        Optional<User> foundUser = data.entrySet().stream()
                .filter(entry -> entry.getKey().equals(userId))
                .map(Map.Entry::getValue)
                .findFirst();

        return foundUser;
    }

    @Override
    public List<User> findByName(String name) {
        return data.values().stream()
                .filter(user -> user.getName().contains(name))
                .toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> foundUser = data.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();

        return foundUser;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID userId) {
        data.remove(userId);
    }
}
