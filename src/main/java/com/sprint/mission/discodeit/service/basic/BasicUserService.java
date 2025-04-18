package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.basic
 * fileName       : BasicUserService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class BasicUserService  implements UserService {
    UserRepository ur;

    public BasicUserService(UserRepository ur) {
        this.ur = ur;
    }

    @Override
    public User createUser(String name) {
        return ur.createUserByName(name);
    }

    @Override
    public User findUserById(UUID userId) {
        return ur.findUserById(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return ur.findAllUsers();
    }

    @Override
    public void updateUser(UUID userId, String name) {
        ur.updateUserById(userId, name);
    }

    @Override
    public void deleteUser(UUID userId) {
        ur.deleteUserById(userId);
    }
}
