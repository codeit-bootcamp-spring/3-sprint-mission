package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * packageName    : com.sprint.mission.discodeit.service.file
 * fileName       : FileUserService
 * author         : doungukkim
 * date           : 2025. 4. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025. 4. 17.        doungukkim       최초 생성
 */
@Service
public class FileUserService implements UserService {

    FileUserRepository fur = new FileUserRepository();

    @Override
    public User createUser(String name) {
        Objects.requireNonNull(name, "no name in parameter: FileUserService.createUser");
        return fur.createUserByName(name);
    }

    @Override
    public User findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: FileUserService.findUserById");
        User result = fur.findUserById(userId);
        Objects.requireNonNull(result, "찾는 User 없음: FileUserService.findUserById");
        return result;
    }

    @Override
    public List<User> findAllUsers() {
        return fur.findAllUsers();
    }

    @Override
    public void updateUser(UUID userId, String name) {
        Objects.requireNonNull(userId, "user 아이디 입력 없음: FileUserService.updateUser");
        Objects.requireNonNull(name, "이름 입력 없음: FileUserService.updateUser");
        fur.updateUserById(userId, name);
    }

    @Override
    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: FileUserService.deleteUser");
        fur.deleteUserById(userId);
    }
}
