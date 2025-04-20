package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final UserRepository userRepository;

    public JCFUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String username) {
        User user = new User(username);
        userRepository.save(user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void update(UUID id, String newUsername) {
        User user = userRepository.findById(id);
        if (user != null) {
            user.updateUsername(newUsername);
            userRepository.save(user); // 수정된 값 저장
        }
    }

    @Override
    public void delete(UUID id) {
        userRepository.delete(id);
    }
}
