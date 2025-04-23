package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.JcfUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service
 * fileName       : JcfUserService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
@Service
public class JcfUserService implements UserService {

    JcfUserRepository jur = new JcfUserRepository();

    public User createUser(String username, String email, String password) {
        Objects.requireNonNull(username, "no name in parameter: JcfUserService.createUser");
        Objects.requireNonNull(email, "no email in parameter: JcfUserService.createUser");
        Objects.requireNonNull(password, "no password in parameter: JcfUserService.createUser");
        return jur.createUserByName(username,email,password);
    }

    public User findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: JcfUserService.findUserById");
        User result = jur.findUserById(userId);
        Objects.requireNonNull(result, "찾는 User 없음: JcfUserService.findUserById");
        return result;
    }

    public List<User> findAllUsers() {
        return jur.findAllUsers();
    }

    public void updateUser(UUID userId, String name) {

        Objects.requireNonNull(userId, "채널 아이디 입력 없음: JcfUserService.updateUser");
        Objects.requireNonNull(name, "이름 입력 없음: JcfUserService.updateUser");
        jur.updateUserById(userId, name);
    }

    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: JcfUserService.deleteUser");
        jur.deleteUserById(userId);

    }

}
