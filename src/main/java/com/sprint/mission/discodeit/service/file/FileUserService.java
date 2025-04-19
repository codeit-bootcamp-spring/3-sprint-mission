package com.sprint.mission.discodeit.service.file;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

public class FileUserService implements UserService {

    private final UserRepository userRepository;

    public FileUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String userName, String email, String password) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("사용자 이름은 비어있을 수 없습니다.");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일은 비어있을 수 없습니다.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 비어있을 수 없습니다.");
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
    public void updateUserName(UUID userId, String newUsername) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID입니다.");
        }
        user.updateUserName(newUsername);
        userRepository.save(user);
    }

    @Override
    public void updateUserEmail(UUID userId, String newEmail) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID입니다.");
        }
        user.updateEmail(newEmail);
        userRepository.save(user);
    }

    @Override
    public void updateUserPassword(UUID userId, String newPassword) {
        if (newPassword == null) {
            throw new IllegalArgumentException("비밀번호는 null일 수 없습니다.");
        }
        if (newPassword.isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 빈 값일 수 없습니다.");
        }
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자 ID입니다.");
        }
        user.updatePassword(newPassword);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }
}
