package com.sprint.mission.discodeit.Repository.jcf;

import com.sprint.mission.discodeit.Repository.UserRepository;
import com.sprint.mission.discodeit.entity.User;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    @Override
    public void init() {

    }

    @Override
    public void save(User user) {

    }

    @Override
    public User loadByIndex(String name) {
        return null;
    }

    @Override
    public User loadById(UUID id) {
        return null;
    }

    @Override
    public List<User> loadAll() {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {

    }
}
