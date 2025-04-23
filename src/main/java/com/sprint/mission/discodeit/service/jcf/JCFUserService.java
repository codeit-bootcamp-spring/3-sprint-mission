package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {

    private final UserRepository userRepository;

    public JCFUserService() {
        this.userRepository = new JCFUserRepository();
    }

    @Override
    public User createUser(String name) {
        return userRepository.save(new User(name));
    }

    @Override
    public User getUser(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user, String newName) {
        user.updateName(newName);
        return userRepository.save(user);
    }

    @Override
    public User deleteUser(User user) {
        return userRepository.delete(user);
    }
}
