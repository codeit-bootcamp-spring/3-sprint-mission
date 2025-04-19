package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Repository.UserRepository;
import com.sprint.mission.discodeit.Repository.file.FileUserRepository;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final UserRepository userRepository;

    public FileUserService() {
        userRepository = new FileUserRepository();
    }

    @Override
    public User createUser(String name) {
        if (getUserByName(name) != null) {
            throw new IllegalArgumentException("[User] 이미 존재하는 사용자 이름입니다. (" + name + ")");
        }

        User user = User.of(name);
        userRepository.save(user);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        return userRepository.loadById(id);
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.loadByName(name);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.loadAll();
        return users.stream().toList();
    }

    @Override
    public void updateUser(UUID id, String name) {
        try {
            userRepository.update(id, name);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteUser(UUID id) {
        try {
            userRepository.deleteById(id);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }
}
