package com.sprint.mission.discodeit.refactor.service.file;

import com.sprint.mission.discodeit.refactor.entity.User2;
import com.sprint.mission.discodeit.refactor.repository.file.FileUserRepository2;
import com.sprint.mission.discodeit.refactor.service.UserService2;

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
public class FileUserService2 implements UserService2 {

    FileUserRepository2 fur = new FileUserRepository2();

    @Override
    public User2 createUser(String name) {
        return fur.createUserByName(name);
    }

    @Override
    public User2 findUserById(UUID userId) {
        return fur.findUserById(userId);
    }

    @Override
    public List<User2> findAllUsers() {
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
