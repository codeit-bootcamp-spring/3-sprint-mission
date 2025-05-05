package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.user.UserCreateResponse;
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
    public UserCreateResponse create(UserCreateRequest userCreateRequest) {
        boolean usernameNotUnique = !userRepository.isUniqueUsername(userCreateRequest.getUsername());
        boolean emailNotUnique = !userRepository.isUniqueEmail(userCreateRequest.getEmail());

        if (usernameNotUnique || emailNotUnique) {
            throw new IllegalStateException("not unique username or email");
        }

        // 이미지 여부 확인
        if (userCreateRequest.getImage() == null) {
            // 이미지 처리 없음
            User user = userRepository.createUserByName(userCreateRequest.getUsername(), userCreateRequest.getEmail(), userCreateRequest.getPassword());
            UserStatus userStatus = userStatusRepository.createUserStatus(user.getId());
            return new UserCreateResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileId(),
                    userStatus.getId()
            );
        } else{
            // 이미지 처리 있음
            UUID profileId = binaryContentRepository.createBinaryContent(userCreateRequest.getImage()).getId();
            User user = userRepository.createUserByName(userCreateRequest.getUsername(), userCreateRequest.getEmail(), userCreateRequest.getPassword(), profileId);
            UserStatus userStatus = userStatusRepository.createUserStatus(user.getId());
            return new UserCreateResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileId(),
                    userStatus.getId()
            );
        }
    }



    @Override
    public UserFindResponse findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: BasicUserService.findUserById");
        User user = userRepository.findUserById(userId); // throw

        UserStatus userStatus = Optional.ofNullable(userStatusRepository.findUserStatusByUserId(userId)).orElseThrow(() -> new IllegalStateException("userStatus is null"));

        boolean online = userStatusRepository.isOnline(userStatus.getId()); // throw

        return new UserFindResponse(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online);
    }


    @Override
    public List<UserFindResponse> findAllUsers() {
        List<User> users = userRepository.findAllUsers(); // emptyList
        List<UserFindResponse> userFindResponses = new ArrayList<>();

        // user fields + online 으로 response 생성
        for (User user : users) {
            UserStatus userStatus = Optional.ofNullable(userStatusRepository.findUserStatusByUserId(user.getId()))
                    .orElseThrow(() -> new RuntimeException("user does not have a userStatus")); // userStatus has to exist (user:userStatus = 1:1)

            boolean online = userStatusRepository.isOnline(userStatus.getId());

            userFindResponses.add(new UserFindResponse(
                    user.getId(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileId(),
                    online));
        }
        return userFindResponses;
    }




    @Override
    public ProfileUploadResponse updateImage(ProfileUploadRequest request) {
        UUID userId = request.userId();
        byte[] newImage = request.image();
        User user = userRepository.findUserById(userId); // throw
        UUID profileId = userRepository.findUserById(userId).getProfileId(); // throw


        if (profileId == null) {
            // 없음 객체 생성
            // binary content 생성
            BinaryContent binaryContent = binaryContentRepository.createBinaryContent(newImage);
            // 프로필 아이디 유저에 추가
            userRepository.updateProfileIdById(userId, binaryContent.getId()); //throw

            user = userRepository.findUserById(userId); //throw
        } else{
            // binary content 프로필 변경
            binaryContentRepository.updateImage(profileId, newImage); // throw
        }
        return new ProfileUploadResponse(
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId());
    }

    // not required
    @Override
    public void updateUser(UUID userId, String name) {
        Objects.requireNonNull(userId, "user 아이디 입력 없음: BasicUserService.updateUser");
        Objects.requireNonNull(name, "이름 입력 없음: BasicUserService.updateUser");
        userRepository.updateUserById(userId, name);
    }


    @Override
    public void deleteUser(UUID userId) {
        Objects.requireNonNull(userId, "no user Id: BasicUserService.deleteUser");

        User user = userRepository.findUserById(userId); // throw

        UserStatus userStatus = Optional.ofNullable(userStatusRepository.findUserStatusByUserId(userId))
                .orElseThrow(() -> new IllegalArgumentException("no userStatus exist"));

        // UserStatus 삭제
        userStatusRepository.deleteById(userStatus.getId()); // throw

        if (user.getProfileId() != null) { // 프로필 있으면
            // BinaryContent 삭제
            binaryContentRepository.deleteBinaryContentById(user.getProfileId()); // throw
        }
        // User 삭제
        userRepository.deleteUserById(userId); // throw
    }
}
