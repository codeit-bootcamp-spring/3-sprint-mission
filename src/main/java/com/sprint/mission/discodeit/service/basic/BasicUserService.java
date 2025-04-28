package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadRequest;
import com.sprint.mission.discodeit.Dto.userStatus.ProfileUploadResponse;
import com.sprint.mission.discodeit.Dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.Dto.user.UserFindResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;

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
@Primary
@Service("basicUserService")
@RequiredArgsConstructor
public class BasicUserService  implements UserService {
    private final UserRepository userRepository;


    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public User createUser(UserCreateRequest userCreateDto) {
        Objects.requireNonNull(userCreateDto.getUsername(), "no name in parameter: BasicUserService.createUser");
        Objects.requireNonNull(userCreateDto.getEmail(), "no email in parameter: BasicUserService.createUser");
        Objects.requireNonNull(userCreateDto.getPassword(), "no password in parameter: BasicUserService.createUser");

        if ((!userRepository.isUniqueUsername(userCreateDto.getUsername())) || (!userRepository.isUniqueEmail(userCreateDto.getEmail()))) {
            throw new RuntimeException("not unique username or email");
        }

        // 이미지 여부 확인
        if (userCreateDto.getImage() == null) {
            // 이미지 처리 없음
            User result = userRepository.createUserByName(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword());
            userStatusRepository.createUserStatus(result.getId());
            return result;
        } else{
            // 이미지 처리 있음
            UUID profileId = binaryContentRepository.createBinaryContent(userCreateDto.getImage()).getId();
            User result = userRepository.createUserByName(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword(), profileId);
            userStatusRepository.createUserStatus(result.getId());
            return result;
        }
    }



    @Override
    public UserFindResponse findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: BasicUserService.findUserById");
        User user = userRepository.findUserById(userId);
        Objects.requireNonNull(user, "찾는 User 없음: BasicUserService.findUserById");

        UserStatus userStatus = userStatusRepository.findUserStatusByUserId(userId);

        if (userStatus == null) {
            throw new RuntimeException("userStatus is null");
        }

        boolean online = userStatusRepository.isOnline(userStatus.getId());


        UserFindResponse userFindDto = new UserFindResponse(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(), user.getProfileId(), online);

        return userFindDto;
    }


    @Override
    public List<UserFindResponse> findAllUsers() {
        List<User> users = userRepository.findAllUsers();
        List<UserFindResponse> userFindDtos = new ArrayList<>();

        // userDto를 users + online으로 매핑
        for (User user : users) {
            UserStatus status = userStatusRepository.findUserStatusByUserId(user.getId());
            boolean online = false;
            if (status != null) {
                online = userStatusRepository.isOnline(status.getId());
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
        UUID userId = request.getUserId();
        byte[] newImage = request.getImage();
        User user = userRepository.findUserById(userId);
        UUID profileId = findUserById(userId).getProfileId();


        if (profileId == null) {
            // 없음 객체 생성
            // binary content 생성
            BinaryContent binaryContent = binaryContentRepository.createBinaryContent(newImage);
            // 프로필 아이디 유저에 추가
            userRepository.updateProfileIdById(userId, binaryContent.getId());

            user = userRepository.findUserById(userId);
        } else{
            // binary content 프로필 변경
            binaryContentRepository.updateImage(profileId, newImage);
        }
        return new ProfileUploadResponse(
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId());
    }

    @Override
    public void updateUser(UUID userId, String name) {
        Objects.requireNonNull(userId, "user 아이디 입력 없음: BasicUserService.updateUser");
        Objects.requireNonNull(name, "이름 입력 없음: BasicUserService.updateUser");
        userRepository.updateUserById(userId, name);
    }

    /*
        [ ] 관련된 도메인도 같이 삭제합니다.
            BinaryContent(프로필), UserStatus
     */
    @Override
    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: BasicUserService.deleteUser");

        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("잘못된 유저아이디");
        }

        // UserStatus 삭제
        UserStatus userStatus = userStatusRepository.findUserStatusByUserId(userId);
        if (userStatus == null) {
            throw new RuntimeException("no userStatus");
        }
        userStatusRepository.deleteById(userStatus.getId());

        // BinaryContent 삭제
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteBinaryContentById(user.getProfileId());

        }
        // User 삭제
        userRepository.deleteUserById(userId);
    }
}
