package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Dto.user.UserCreateResponse;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadRequest;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadResponse;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserFindResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.jcf.JcfBinaryContentRepostory;
import com.sprint.mission.discodeit.repository.jcf.JcfUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JcfUserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
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
@Profile("jcf")
public class JcfUserService implements UserService {
    private final JcfUserRepository jcfUserRepository;
    private final JcfUserStatusRepository jcfUserStatusRepository;
    private final JcfBinaryContentRepostory jcfBinaryContentRepostory;

    public UserCreateResponse create(UserCreateRequest userCreateDto) {
        Objects.requireNonNull(userCreateDto.getUsername(), "no name in parameter: BasicUserService.createUser");
        Objects.requireNonNull(userCreateDto.getEmail(), "no email in parameter: BasicUserService.createUser");
        Objects.requireNonNull(userCreateDto.getPassword(), "no password in parameter: BasicUserService.createUser");

        if ((!jcfUserRepository.isUniqueUsername(userCreateDto.getUsername())) || (!jcfUserRepository.isUniqueEmail(userCreateDto.getEmail()))) {
            throw new RuntimeException("not unique username or email");
        }

        // 이미지 여부 확인
        if (userCreateDto.getImage() == null) {
            // 이미지 처리 없음
            User user = jcfUserRepository.createUserByName(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword());
            UserStatus userStatus = jcfUserStatusRepository.createUserStatus(user.getId());
            return new UserCreateResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileId(),
                    userStatus.getId()
            );
        } else{
            // 이미지 처리 있음
            UUID binaryContentId = jcfBinaryContentRepostory.createBinaryContent(userCreateDto.getImage()).getId();
            User user = jcfUserRepository.createUserByName(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword(), binaryContentId);
            UserStatus userStatus = jcfUserStatusRepository.createUserStatus(user.getId());
            return new UserCreateResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileId(),
                    userStatus.getId()
            );
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
        List<User> users = jcfUserRepository.findAllUsers();
        List<UserFindResponse> userFindDtos = new ArrayList<>();

        // userDto를 (users + online)으로 매핑
        for (User user : users) {
            UserStatus status = jcfUserStatusRepository.findUserStatusByUserId(user.getId());
            boolean online = false;
            if (status != null) {
                online = jcfUserStatusRepository.isOnline(status.getId());
            }

            userFindDtos.add(new UserFindResponse(
                    user.getId(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileId(),
                    online));
        }

        return userFindDtos;
    }

    @Override
    public ProfileUploadResponse updateImage(ProfileUploadRequest request) {
        UUID userId = request.userId();
        byte[] newImage = request.image();
        User user = jcfUserRepository.findUserById(userId);
        UUID profileId = findUserById(userId).profileId();


        if (profileId == null) {
            // 없음 객체 생성
            // binary content 생성
            BinaryContent binaryContent = jcfBinaryContentRepostory.createBinaryContent(newImage);
            // 프로필 아이디 유저에 추가
            jcfUserRepository.updateProfileIdById(userId, binaryContent.getId());

            user = jcfUserRepository.findUserById(userId);
        } else {
            // binary content 프로필 변경
            jcfBinaryContentRepostory.updateImage(profileId, newImage);
        }
        return new ProfileUploadResponse(
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId());
    }

    public void updateUser(UUID userId, String name) {
        Objects.requireNonNull(userId, "채널 아이디 입력 없음: JcfUserService.updateUser");
        Objects.requireNonNull(name, "이름 입력 없음: JcfUserService.updateUser");
        jcfUserRepository.updateUserById(userId, name);
    }

    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: JcfUserService.deleteUser");

        User user = jcfUserRepository.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("잘못된 유저아이디");
        }

        // UserStatus 삭제
        UserStatus userStatus = jcfUserStatusRepository.findUserStatusByUserId(userId);
        if (userStatus == null) {
            throw new RuntimeException("no userStatus");
        }

        // BinaryContent 삭제
        if (user.getProfileId() != null) {
            jcfBinaryContentRepostory.deleteBinaryContentById(user.getProfileId());
        }

        jcfUserStatusRepository.deleteById(userStatus.getId());
        // User 삭제
        jcfUserRepository.deleteUserById(userId);
    }

}
