package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.Dto.user.UserCreateDto;
import com.sprint.mission.discodeit.Dto.user.UserFindDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
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
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public User createUser(UserCreateDto userCreateDto) {
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
            UUID binaryContentId = binaryContentRepository.createBinaryContent(userCreateDto.getImage()).getId();
            User result = userRepository.createUserByName(userCreateDto.getUsername(), userCreateDto.getEmail(), userCreateDto.getPassword(), binaryContentId);
            userStatusRepository.createUserStatus(result.getId());
            return result;
        }


    }

/*
     이미지만 저장하는 로직 추가(BinaryContent 객체 생성)
 */

    @Override
    public UserFindDto findUserById(UUID userId) {
        Objects.requireNonNull(userId, "User 아이디 입력 없음: BasicUserService.findUserById");
        User user = userRepository.findUserById(userId);
        Objects.requireNonNull(user, "찾는 User 없음: BasicUserService.findUserById");

        UserStatus userStatus = userStatusRepository.findUserStatusByUserId(userId);

        if (userStatus == null) {
            throw new RuntimeException("userStatus is null");
        }

        boolean online = userStatusRepository.isOnline(userStatus.getId());


        UserFindDto userFindDto = new UserFindDto(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(), user.getProfileId(), online);

        return userFindDto;
    }


    @Override
    public List<UserFindDto> findAllUsers() {
        List<User> users = userRepository.findAllUsers();
        List<UserFindDto> userFindDtos = new ArrayList<>();

        // userDto를 users + online으로 매핑
        for (User user : users) {
            UserStatus status = userStatusRepository.findUserStatusByUserId(user.getId());
            boolean online = false;
            if (status != null) {
                online = userStatusRepository.isOnline(status.getId());
            }

            userFindDtos.add(new UserFindDto(
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
