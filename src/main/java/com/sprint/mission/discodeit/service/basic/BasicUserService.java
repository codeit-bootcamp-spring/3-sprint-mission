package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@ComponentScan(basePackages = "com.example.mapper")
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    //
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;

    private static final String SERVICE_NAME = "[UserService]";


    /**
     * User create 로직
     *
     * 1. 유저의 이름, 이메일, 비밀번호 정보를 담은 userCreateRequest 받는다.
     * 2. 유저의 profile 정보(fileName, contentType, bytes)를 담은 optionalProfileCreateRequest를 받는다.
     * 3. userCreateRequest의 정보를 가공하여, User의 기본 정보를 생성한다.
     * 4. optionalProfileCreateRequest의 정보를 가공하여, User의 profile 기본 정보를 생성한다.
     * 5. 생성된 profile과 User의 기본 정보를 바탕으로 new User를 통해 새로운 User를 생성한다.
     * 6. UserMapper를 통해 User를 Dto로 가공한 UserDto를 반환한다.
     *
     * @param userCreateRequest
     * @param optionalProfileCreateRequest
     *
     * @return UserDto
     * */
    @Override
    @Transactional
    public UserDto create(UserCreateRequest userCreateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        // DTO -> Entity 시작
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();
//        UserStatus status = userCreateRequest.status();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(SERVICE_NAME + " User with email " + email + " already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    SERVICE_NAME + " User with username " + username + " already exists");
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
            .map(profileRequest -> {
                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent); // profile 기본 정보 저장
                binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes()); // profile 정보 저장
                return binaryContent;
            })
            .orElse(null);
        String password = userCreateRequest.password();

        User user = new User(username, email, password, nullableProfile);
        // save 통해 user를 영속화하면, 이후 변경 감지를 통해 트랜잭션 종료 시 자동으로 flush 됨 (dirty checking)
        userRepository.saveAndFlush(user);

        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user, now);
        user.setStatus(userStatus);
        userStatusRepository.save(userStatus);

        return userMapper.toDto(user);
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
            .map(userMapper::toDto)
            .orElseThrow(() -> new NoSuchElementException(SERVICE_NAME + " User with id " + userId + " not found"));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAllWithProfileAndStatus()
            .stream()
            .map(userMapper::toDto)
            .toList();
    }

    /**
     * User update 로직
     *
     * 1. 유저의 이름, 이메일, 비밀번호 정보를 담은 userUpdateRequest 받는다.
     * 2. 유저의 profile 정보(fileName, contentType, bytes)를 담은 optionalProfileCreateRequest를 받는다.
     * 3. 첫번째 파라미터 userId에 해당하는 user를 find한다.
     * 3. userUpdateRequest의 정보를 가공하여, User의 새로운 정보를 생성한다.
     * 4. optionalProfileCreateRequest의 정보를 가공하여, User의 profile 기본 정보를 생성한다.
     * 5. 수정된 profile과 User의 기본 정보를 바탕으로 new User를 통해 새로운 User를 생성한다.
     * 6. UserMapper를 통해 User를 Dto로 가공한 UserDto를 반환한다.
     *
     * @param userId
     * @param userUpdateRequest
     * @param optionalProfileCreateRequest
     *
     * @return UserDto
     * */
    @Override
    @Transactional
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(SERVICE_NAME + " User with id " + userId + " not found"));

        // 부분 업데이트: null이 아닌 필드만 업데이트
        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();
        
        // username 업데이트 (null이 아닌 경우에만)
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            if (userRepository.existsByUsername(newUsername) && !newUsername.equals(user.getUsername())) {
                throw new IllegalArgumentException(
                    SERVICE_NAME + " User with username " + newUsername + " already exists");
            }
        } else {
            newUsername = user.getUsername(); // 현재 값 유지
        }
        
        // email 업데이트 (null이 아닌 경우에만)
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            if (userRepository.existsByEmail(newEmail) && !newEmail.equals(user.getEmail())) {
                throw new IllegalArgumentException(SERVICE_NAME + " User with email " + newEmail + " already exists");
            }
        } else {
            newEmail = user.getEmail(); // 현재 값 유지
        }
        
        // password 업데이트 (null이 아닌 경우에만)
        if (newPassword == null || newPassword.trim().isEmpty()) {
            newPassword = user.getPassword(); // 현재 값 유지
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
            .map(profileRequest -> {
                BinaryContent existingProfile = user.getProfile();
                if (existingProfile != null && existingProfile.getId() != null) {
                    binaryContentRepository.deleteById(existingProfile.getId());
                }

                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);

                binaryContentRepository.save(binaryContent); // profile 기본 정보 저장
                binaryContentStorage.put(binaryContent.getId(), bytes); // profile bytes 정보 저장
                return binaryContent;
            })
            .orElse(user.getProfile()); // 현재 profile 유지

        // 새로운 user 기본 정보와, profile 정보로 갱신
        user.update(newUsername, newEmail, newPassword, nullableProfile);

        // 변경 감지를 통해 트랜잭션 종료 시 자동으로 flush 됨 (dirty checking)
        return userMapper.toDto(user); // save 호출 없이도 변경된 필드 자동 업데이트
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        if (userRepository.existsById(userId)) {
            throw new NoSuchElementException(SERVICE_NAME + " User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }
}
