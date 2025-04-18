package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.service.file
 * fileName       : FileUserService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class FileUserService implements UserService {

    FileUserRepository fur = new FileUserRepository();

    @Override
    public User createUser(String name) {
        return fur.createUserByName(name);
    }

    @Override
    public User findUserById(UUID userId) {
        return fur.findUserById(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return fur.findAllUsers();
    }

    @Override
    public void updateUser(UUID userId, String name) {
        fur.updateUserById(userId, name);
    }

    @Override
    public void deleteUser(UUID userId) {
        fur.deleteUserById(userId);
    }
}
