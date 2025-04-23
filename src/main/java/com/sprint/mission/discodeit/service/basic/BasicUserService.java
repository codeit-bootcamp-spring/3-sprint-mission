package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.swing.*;
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
@Service("basicUserService")
@RequiredArgsConstructor
@Primary
public class BasicUserService  implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(String username, String email, String password) {
        Objects.requireNonNull(username, "no name in parameter: BasicUserService.createUser");
        Objects.requireNonNull(email, "no email in parameter: BasicUserService.createUser");
        Objects.requireNonNull(password, "no password in parameter: BasicUserService.createUser");
        return userRepository.createUserByName(username, email, password);
    }

    @Override
    public User findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: BasicUserService.findUserById");
        User result = userRepository.findUserById(userId);
        Objects.requireNonNull(result, "찾는 User 없음: BasicUserService.findUserById");
        return result;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    public void updateUser(UUID userId, String name) {
        Objects.requireNonNull(userId, "user 아이디 입력 없음: BasicUserService.updateUser");
        Objects.requireNonNull(name, "이름 입력 없음: BasicUserService.updateUser");
        userRepository.updateUserById(userId, name);
    }

    @Override
    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: BasicUserService.deleteUser");
        userRepository.deleteUserById(userId);
    }
}
