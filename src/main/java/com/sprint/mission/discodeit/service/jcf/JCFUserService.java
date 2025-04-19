package com.sprint.mission.discodeit.service.jcf;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {

    private final UserRepository userRepository;

    public JCFUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String username, String email, String password) {
        User user = new User(username, email, password);
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
    public void updateUserName(UUID userId, String newUsername) {
        User user = userRepository.findById(userId);
        if (user != null) {
            user.updateUserName(newUsername);
            userRepository.save(user);
        }
    }

    @Override
    public void updateUserEmail(UUID userId, String newEmail) {
        User user = userRepository.findById(userId);
        if (user != null && newEmail != null && !newEmail.isEmpty()) {
            user.updateEmail(newEmail);
            userRepository.save(user);
        }
    }

    @Override
    public void updateUserPassword(UUID userId, String newPassword) {
        User user = userRepository.findById(userId);
        if (user != null) {
            if (newPassword == null || newPassword.isEmpty()) {
                throw new IllegalArgumentException("비밀번호는 null이거나 빈 문자열일 수 없습니다.");
            }
            user.updatePassword(newPassword);
            userRepository.save(user);
        }
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }
}
