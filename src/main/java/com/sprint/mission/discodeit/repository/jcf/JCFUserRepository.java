package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data = new HashMap<>();

    public User save(User user) {
        return data.put(user.getId(), user);
    }

    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(data.get(userId));
    }

    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    public void deleteById(UUID userId) {
        data.remove(userId);
    }
}