package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Dto.UserCreateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileUserBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class FileUserService implements UserService {

    private final FileUserRepository fileUserRepository;
    private final FileUserBinaryContentRepository fileUserBinaryContentRepository;
    private final FileUserStatusRepository fileUserStatusRepository;




    // 이미지 저장 로직 추가
    @Override
    public User createUser(UserCreateDto userCreateDto) {
        Objects.requireNonNull(userCreateDto.getUsername(), "no name in parameter: BasicUserService.createUser");
        Objects.requireNonNull(userCreateDto.getEmail(), "no email in parameter: BasicUserService.createUser");
        Objects.requireNonNull(userCreateDto.getPassword(), "no password in parameter: BasicUserService.createUser");

        if ((!fileUserRepository.isUniqueUsername(userCreateDto.getUsername())) || (!fileUserRepository.isUniqueEmail(userCreateDto.getEmail()))) {
            throw new RuntimeException("not unique username or email");
        }

        // 이미지 여부 확인
        if (userCreateDto.getImage() == null) {
            // 이미지 처리 없음
            User result = fileUserRepository.createUserByName(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword());
            fileUserStatusRepository.createUserStatus(result.getId());
            return result;
        } else{
            // 이미지 처리 있음
            UUID binaryContentId = fileUserBinaryContentRepository.createBinaryContent(userCreateDto.getImage()).getId();
            User result = fileUserRepository.createUserByName(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword(), binaryContentId);
            fileUserStatusRepository.createUserStatus(result.getId());
            return result;
        }

    }

    @Override
    public User findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: FileUserService.findUserById");
        User result = fileUserRepository.findUserById(userId);
        Objects.requireNonNull(result, "찾는 User 없음: FileUserService.findUserById");
        return result;
    }

    @Override
    public List<User> findAllUsers() {
        return fileUserRepository.findAllUsers();
    }

    @Override
    public void updateUser(UUID userId, String name) {
        Objects.requireNonNull(userId, "user 아이디 입력 없음: FileUserService.updateUser");
        Objects.requireNonNull(name, "이름 입력 없음: FileUserService.updateUser");
        fileUserRepository.updateUserById(userId, name);
    }

    @Override
    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: FileUserService.deleteUser");
        fileUserRepository.deleteUserById(userId);
    }
}
