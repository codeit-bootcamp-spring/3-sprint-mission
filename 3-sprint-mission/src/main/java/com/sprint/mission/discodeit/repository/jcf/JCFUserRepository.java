package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserRepository implements UserRepository {
    private final List<User> data;

    public JCFUserRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public User save(User user){
        this.data.add(user);
        return user;
    }

    @Override
    public List<User>  findAll() {
        return this.data;
    }

    @Override
    public User find(UUID id) {
        return this.data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public User findByUsername(String username) {
        return this.data.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<User> findByName(String name) {
        return this.data.stream()
                .filter(u -> u.getName().contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public User findByEmail(String email) {
        return this.data.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public boolean existsId(UUID id) {
        return this.data.stream().anyMatch(u -> u.getId().equals(id));
    }

    @Override
    public boolean existsUsername(String username) {
        return this.data.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    @Override
    public boolean existsEmail(String email) {
        return this.data.stream().anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public boolean existsName(String name) {
        return this.data.stream().anyMatch(u -> u.getName().equals(name));
    }

    @Override
    public void delete(UUID id) {
        this.data.removeIf(u -> u.getId().equals(id));
    }
}
