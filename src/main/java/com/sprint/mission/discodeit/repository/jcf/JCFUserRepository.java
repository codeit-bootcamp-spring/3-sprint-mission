package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf")
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        data = new ConcurrentHashMap<>();
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
    public List<User> findByNameContaining(String name) {
        return data.values().stream()
                .filter(user -> user.getName().contains(name))
                .toList();
    }

    @Override
    public Optional<User> findByName(String name) {
        Optional<User> foundUser = data.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst();

        return foundUser;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> foundUser = data.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();

        return foundUser;
    }

    @Override
    public Optional<User> findByNameAndPassword(String name, String password) {
        Optional<User> foundUser = data.values().stream()
                .filter(user -> user.getName().equals(name) && user.getPassword().equals(password))
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
