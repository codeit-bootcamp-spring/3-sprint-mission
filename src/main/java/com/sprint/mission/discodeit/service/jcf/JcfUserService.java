package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Dto.UserCreateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JcfBinaryContentRepostory;
import com.sprint.mission.discodeit.repository.jcf.JcfUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JcfUserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JcfUserService implements UserService {

    private final JcfUserRepository jcfUserRepository;
    private final JcfUserStatusRepository jcfUserStatusRepository;
    private final JcfBinaryContentRepostory jcfBinaryContentRepostory;



    public User createUser(UserCreateDto userCreateDto) {
        Objects.requireNonNull(userCreateDto.getUsername(), "no name in parameter: BasicUserService.createUser");
        Objects.requireNonNull(userCreateDto.getEmail(), "no email in parameter: BasicUserService.createUser");
        Objects.requireNonNull(userCreateDto.getPassword(), "no password in parameter: BasicUserService.createUser");

        if ((!jcfUserRepository.isUniqueUsername(userCreateDto.getUsername())) || (!jcfUserRepository.isUniqueEmail(userCreateDto.getEmail()))) {
            throw new RuntimeException("not unique username or email");
        }

        // 이미지 여부 확인
        if (userCreateDto.getImage() == null) {
            // 이미지 처리 없음
            User result = jcfUserRepository.createUserByName(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword());
            jcfUserStatusRepository.createUserStatus(result.getId());
            return result;
        } else{
            // 이미지 처리 있음
            UUID binaryContentId = jcfBinaryContentRepostory.createBinaryContent(userCreateDto.getImage()).getId();
            User result = jcfUserRepository.createUserByName(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword(), binaryContentId);
            jcfUserStatusRepository.createUserStatus(result.getId());
            return result;
        }

    }

    public User findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: JcfUserService.findUserById");
        User result = jcfUserRepository.findUserById(userId);
        Objects.requireNonNull(result, "찾는 User 없음: JcfUserService.findUserById");
        return result;
    }

    public List<User> findAllUsers() {
        return jcfUserRepository.findAllUsers();
    }

    public void updateUser(UUID userId, String name) {

        Objects.requireNonNull(userId, "채널 아이디 입력 없음: JcfUserService.updateUser");
        Objects.requireNonNull(name, "이름 입력 없음: JcfUserService.updateUser");
        jcfUserRepository.updateUserById(userId, name);
    }

    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: JcfUserService.deleteUser");
        jcfUserRepository.deleteUserById(userId);

    }

}
