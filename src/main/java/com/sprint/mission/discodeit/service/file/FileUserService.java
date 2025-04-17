package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private final FileDataStore<User> store;
    private final Map<UUID, User> data;

    public FileUserService() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        this.store = new FileDataStore<>("data/users.ser");
        this.data = store.load();
    }

    @Override
    public User createUser(String name) {
        User user = new User(name);
        data.put(user.getId(), user);
        store.save(data);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        User user = data.get(id);
        if (user == null) throw new NoSuchElementException("User not found: " + id);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateUser(User user, String newName) {
        user.updateName(newName);
        store.save(data);
        return user;
    }

    @Override
    public User deleteUser(User user) {
        User removed = data.remove(user.getId());
        store.save(data);
        return removed;
    }
}
