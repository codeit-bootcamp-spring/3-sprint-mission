package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JcfUserRepository;
import com.sprint.mission.discodeit.service.UserService;

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
public class JcfUserService implements UserService {

    JcfUserRepository jur = new JcfUserRepository();

    public User createUser(String name) {
        return jur.createUserByName(name);
    }

    public User findUserById(UUID userId) {
        return jur.findUserById(userId);

    }

    public List<User> findAllUsers() {
        return jur.findAllUsers();
    }

    public void updateUser(UUID userId, String name) {
        jur.updateUserById(userId, name);
    }

    public void deleteUser(UUID userId) {
        jur.deleteUserById(userId);

    }

}
