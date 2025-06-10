package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.serviceDto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional(
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public UserDto create(UserCreateRequest userCreateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        // DTO -> Entity 시작
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();
//        UserStatus status = userCreateRequest.status();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                "User with username " + username + " already exists");
        }

        UUID nullableProfileId = optionalProfileCreateRequest
            .map(profileRequest -> {
                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                try {
                    binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return binaryContentRepository.save(binaryContent).getId();
            })
            .orElse(null);

        BinaryContent profile = nullableProfileId != null ? binaryContentRepository
            .findById(nullableProfileId)
            .orElse(null) : null; // nullableProfileId가 null 아니면 찾아오고, null이면 null 할당

        String password = userCreateRequest.password();

        User user = new User(username, email, password, profile);
        // DTO -> Entity 완료
        User createdUser = userRepository.save(
            user); // 트랜잭션 실행, profile도 같이 저장

        UserDto userDto = userMapper.toDto(createdUser);

        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(createdUser, now);
        userStatusRepository.save(userStatus); // 트랜잭션 실행

        return userDto;
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
            .map(userMapper::toDto)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
            .stream()
            .map(userMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
        if (userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException(
                "User with username " + newUsername + " already exists");
        }

        UUID nullableProfileId = optionalProfileCreateRequest
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

                try {
                    binaryContentStorage.put(binaryContent.getId(), bytes); // 파일 저장 추가
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return binaryContentRepository.save(binaryContent).getId();
            })
            .orElse(null);

        // 프로필 업데이트가 있을 경우 변경된 profile을 설정
        BinaryContent profile =
            nullableProfileId != null ? binaryContentRepository.findById(nullableProfileId)
                .orElse(null) : null;
        String newPassword = userUpdateRequest.newPassword();

        user.update(newUsername, newEmail, newPassword, profile);

        // 트랜잭션 종료 시 변경된 값만 자동으로 DB 반영
        return userMapper.toDto(user); // save 호출 없이도 변경된 필드 자동 업데이트
    }

    @Override
    @Transactional(
        rollbackFor = Exception.class,
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED)
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        Optional.ofNullable(user.getProfile().getId())
            .ifPresent(binaryContentRepository::deleteById);
        userStatusRepository.deleteByUserId(userId);

        userRepository.deleteById(userId);
    }
}
