package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Dto.user.ProfileUploadRequest;
import com.sprint.mission.discodeit.Dto.user.ProfileUploadResponse;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserFindResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.jcf.JcfBinaryContentRepostory;
import com.sprint.mission.discodeit.repository.jcf.JcfUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JcfUserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class JcfUserService implements UserService
{

    private final JcfUserRepository jcfUserRepository;
    private final JcfUserStatusRepository jcfUserStatusRepository;
    private final JcfBinaryContentRepostory jcfBinaryContentRepostory;



    public User createUser(UserCreateRequest userCreateDto) {
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

    public UserFindResponse findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: JcfUserService.findUserById");
        User user = jcfUserRepository.findUserById(userId);
        Objects.requireNonNull(user, "찾는 User 없음: JcfUserService.findUserById");

        // 온라인 확인 로직
        // 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.
        UserStatus userStatus = jcfUserStatusRepository.findUserStatusByUserId(userId);
        if (userStatus == null) {
            throw new RuntimeException("userStatus is null");
        }
        boolean online = jcfUserStatusRepository.isOnline(userStatus.getId());


        UserFindResponse userFindDto = new UserFindResponse(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(), user.getProfileId(), online);

        return userFindDto;
    }

    public List<UserFindResponse> findAllUsers() {
        List<UserStatus> allUserStatus = jcfUserStatusRepository.findAllUserStatus();
        List<User> users = jcfUserRepository.findAllUsers();

        List<UserFindResponse> userFindDtos = new ArrayList<>();

        // userDto를 users + online으로 매핑
        for (int i =0;i<users.size();i++) {
            if (allUserStatus.get(i) == null) {
                throw new RuntimeException("userStatus n users are not 1:1");
            }
            boolean online = jcfUserStatusRepository.isOnline(allUserStatus.get(i).getId());

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
