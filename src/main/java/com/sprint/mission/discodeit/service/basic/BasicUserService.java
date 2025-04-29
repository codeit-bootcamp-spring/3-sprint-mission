package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public User find(UUID userId) {
        return this.userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

    }

    @Override
    public List<User> find(String name) {
        return List.of();
    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String newName, int newAge, String newEmail, String newPassword) {
        User user = this.find(userId);
        user.update(newName, newAge, newEmail, newPassword);
        return this.create(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        this.userRepository.deleteById(userId);
    }
}
