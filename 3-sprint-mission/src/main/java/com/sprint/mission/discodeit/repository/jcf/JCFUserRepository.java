package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

//@Repository
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
    public Optional<User> findById(UUID id) {
        return this.data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.data.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return this.data.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.stream().anyMatch(u -> u.getId().equals(id));
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.data.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.data.stream().anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public void deleteById(UUID id) {
        this.data.removeIf(u -> u.getId().equals(id));
    }
}
