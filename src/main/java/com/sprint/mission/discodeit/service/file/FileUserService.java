package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.Dto.user.ProfileUploadRequest;
import com.sprint.mission.discodeit.Dto.user.ProfileUploadResponse;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserFindResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.file.FileUserBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public User createUser(UserCreateRequest userCreateDto) {
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
        } else {
            // 이미지 처리 있음
            UUID binaryContentId = fileUserBinaryContentRepository.createBinaryContent(userCreateDto.getImage()).getId();
            User result = fileUserRepository.createUserByName(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword(), binaryContentId);
            fileUserStatusRepository.createUserStatus(result.getId());
            return result;
        }

    }

    @Override
    public UserFindResponse findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: FileUserService.findUserById");
        User user = fileUserRepository.findUserById(userId);
        Objects.requireNonNull(user, "찾는 User 없음: FileUserService.findUserById");
        // 온라인 확인 로직
        // 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.
        UserStatus userStatus = fileUserStatusRepository.findUserStatusByUserId(userId);


        if (userStatus == null) {
            throw new RuntimeException("userStatus is null");
        }

        boolean online = fileUserStatusRepository.isOnline(userStatus.getId());


        UserFindResponse userFindDto = new UserFindResponse(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(), user.getProfileId(), online);

        return userFindDto;
    }

    @Override
    public List<UserFindResponse> findAllUsers() {
        List<UserStatus> allUserStatus = fileUserStatusRepository.findAllUserStatus();
        List<User> users = fileUserRepository.findAllUsers();

        List<UserFindResponse> userFindDtos = new ArrayList<>();

        // userDto를 (users + online)으로 매핑
        for (int i =0;i<users.size();i++) {
            if (allUserStatus.get(i) == null) {
                throw new RuntimeException("userStatus is null");
            }
            boolean online = fileUserStatusRepository.isOnline(allUserStatus.get(i).getId());

            userFindDtos.add(new UserFindResponse(
                    users.get(i).getId(),
                    users.get(i).getCreatedAt(),
                    users.get(i).getUpdatedAt(),
                    users.get(i).getUsername(),
                    users.get(i).getEmail(),
                    users.get(i).getProfileId(),
                    online));
        }

        return userFindDtos;
    }

    // 완성 필요
    @Override
    public ProfileUploadResponse updateImage(ProfileUploadRequest request) {
        return null;
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
