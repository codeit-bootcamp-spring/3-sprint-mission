package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
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
        Objects.requireNonNull(name, "no name in parameter: BasicUserService.createUser");
        return ur.createUserByName(name);
    }

    @Override
    public User findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: BasicUserService.findUserById");
        User result = ur.findUserById(userId);
        Objects.requireNonNull(result, "찾는 User 없음: BasicUserService.findUserById");
        return result;
    }

    @Override
    public List<User> findAllUsers() {
        return ur.findAllUsers();
    }

    @Override
    public void updateUser(UUID userId, String name) {
        Objects.requireNonNull(userId, "user 아이디 입력 없음: BasicUserService.updateUser");
        Objects.requireNonNull(name, "이름 입력 없음: BasicUserService.updateUser");
        ur.updateUserById(userId, name);
    }

    @Override
    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: BasicUserService.deleteUser");
        ur.deleteUserById(userId);
    }
}
