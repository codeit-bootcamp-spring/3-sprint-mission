package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.file.FileDataStore;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final FileDataStore<User> store;
    private final Map<UUID, User> data;

    public FileUserRepository() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        this.store = new FileDataStore<>("data/users.ser");
        this.data = store.load();
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        store.save(data);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User delete(User user) {
        User removed = data.remove(user.getId());
        store.save(data);
        return removed;
    }
}