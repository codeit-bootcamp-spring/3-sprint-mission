package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Repository.file.FileUserRepository;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private User user;
    private final FileUserRepository fileUserRepository = new FileUserRepository();

    @Override
    public User createUser(String name) {
        // 이름 중복 검사
        if (getUserByName(name) != null) {
            System.out.println("[User] 이름 중복 검사");
            System.out.println("[User] 이미 존재하는 사용자 이름입니다. (" + name + ")");
            return null;
        }

        User user = User.of(name);
        fileUserRepository.save(user);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        if (id == null) {
            System.out.println("[User] id 값이 유효하지 않습니다. (" + id + ")");
            return null;
        }

        User user = fileUserRepository.loadById(id);
        if (user == null) {
            System.out.println("[User] 사용자 조회");
            return null;
        }

        return user;
    }

    @Override
    public User getUserByName(String name) {
        return fileUserRepository.loadByIndex(name);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = fileUserRepository.loadAll();
        return users.stream().toList();
    }

    @Override
    public void updateUser(UUID id, String name) {

    }

    @Override
    public void deleteUser(UUID id) {

    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }
}
