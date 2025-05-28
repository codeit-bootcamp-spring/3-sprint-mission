package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JcfUserRepository implements UserRepository {

    private final Map<UUID, User> userMap;

    public JcfUserRepository() {
        this.userMap = new ConcurrentHashMap<>();
    }

    @Override
    public User save(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.findAll().stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User update(User user) {
        if (!userMap.containsKey(user.getId())) {
            throw new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다: " + user.getId());
        }
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        if (!userMap.containsKey(id)) {
            throw new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다: " + id);
        }
        userMap.remove(id);
    }

    @Override
    public boolean existsById(UUID channelId) {
        return userMap.containsKey(channelId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.findAll().stream()
            .anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.findAll().stream()
            .anyMatch(user -> user.getEmail().equals(email));
    }

}