package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String userName, String email, String password) {
        if (userName == null || email == null || password == null) {
            throw new IllegalArgumentException("모든 필드는 필수입니다.");
        }
        User user = new User(userName, email, password);
        return userRepository.save(user);
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void updateUserName(UUID userId, String newUserName) {
        User user = userRepository.findById(userId);
        if (user != null) {
            user.updateUserName(newUserName);
            userRepository.save(user);
        }
    }

    @Override
    public void updateUserEmail(UUID userId, String newEmail) {
        User user = userRepository.findById(userId);
        if (user != null) {
            user.updateEmail(newEmail);
            userRepository.save(user);
        }
    }

    @Override
    public void updateUserPassword(UUID userId, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 null 또는 빈 문자열일 수 없습니다.");
        }
        User user = userRepository.findById(userId);
        if (user != null) {
            user.updatePassword(newPassword);
            userRepository.save(user);
        }
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }
}
