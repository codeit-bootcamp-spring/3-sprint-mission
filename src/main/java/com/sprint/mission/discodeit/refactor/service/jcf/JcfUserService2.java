package com.sprint.mission.discodeit.refactor.service.jcf;

import com.sprint.mission.discodeit.refactor.entity.User2;
import com.sprint.mission.discodeit.refactor.repository.jcf.JcfUserRepository2;
import com.sprint.mission.discodeit.refactor.service.UserService2;

import java.util.List;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.refactor.service
 * fileName       : JcfUserService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
public class JcfUserService2 implements UserService2 {

    JcfUserRepository2 jur = new JcfUserRepository2();

    public User2 createUser(String name) {
        return jur.createUserByName(name);
    }

    public User2 findUserById(UUID userId) {
        return jur.findUserById(userId);

    }

    public List<User2> findAllUsers() {
        return jur.findAllUsers();
    }

    public void updateUser(UUID userId, String name) {
        jur.updateUserById(userId, name);
    }

    public void deleteUser(UUID userId) {
        jur.deleteUserById(userId);

    }

}
