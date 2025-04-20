package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {

    UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String RRN, String name, int age, String email) {
        User user = new User(RRN, name, age, email);

        userRepository.create(user);

        return userRepository.findById(user.getId());
    }

    @Override
    public User foundUser(UUID id) {

        return userRepository.findById(id);
    }

    @Override
    public List<User> readAllUsers() {

        return userRepository.findAll();
    }

    @Override
    public User updateUser(UUID id, String newName, String newEmail) {
        User user = userRepository.findById(id);
        userRepository.update(user.getId(), newName, newEmail);

        return userRepository.findById(id);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id);
        userRepository.delete(id);
    }
}
