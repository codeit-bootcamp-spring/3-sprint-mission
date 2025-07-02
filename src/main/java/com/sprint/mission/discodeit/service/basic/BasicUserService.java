package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        log.info("사용자 생성 중 - username: {}, email: {}, 프로필 이미지: {}",
            username, email, optionalProfileCreateRequest.isPresent() ? "있음" : "없음");

        if (userRepository.existsByEmail(email)) {
            log.warn("사용자 생성 실패 - 중복된 이메일: {}", email);
            throw new EmailAlreadyExistsException(email);
        }
        if (userRepository.existsByUsername(username)) {
            log.warn("사용자 생성 실패 - 중복된 사용자명: {}", username);
            throw new UserNameAlreadyExistsException(username);
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
            .map(profileRequest -> {
                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();

                log.debug("프로필 이미지 저장 중 - 파일명: {}, 타입: {}, 크기: {} bytes",
                    fileName, contentType, bytes.length);

                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes);

                log.debug("프로필 이미지 저장 완료 - 파일명: {}, 타입: {}, 크기: {} bytes",
                    fileName, contentType, bytes.length);

                return binaryContent;
            })
            .orElse(null);

        String password = userCreateRequest.password();

        User user = new User(username, email, password, nullableProfile);
        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user, now);

        userRepository.save(user);

        log.info("사용자 생성 완료 - userId: {}, username: {}, email: {}",
            user.getId(), username, email);

        return userMapper.toDto(user);
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
            .map(userMapper::toDto)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAllWithProfileAndStatus()
            .stream()
            .map(userMapper::toDto)
            .toList();
    }

    @Transactional
    @Override
    public UserDto update(UUID userId,
        UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("사용자 수정 실패 - 존재하지 않는 ID: {}", userId);
                return new UserNotFoundException(userId);
            });

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();

        log.info("사용자 수정 중 - userId: {}, newUsername: {}, newEmail: {}, 프로필 이미지: {}",
            userId, newUsername, newEmail,
            optionalProfileCreateRequest.isPresent() ? "변경" : "변경없음");

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            log.warn("사용자 수정 실패 - 중복된 이메일: {}", newEmail);
            throw new EmailAlreadyExistsException(newEmail);
        }
        if (!user.getUsername().equals(newUsername) && userRepository.existsByUsername(
            newUsername)) {
            log.warn("사용자 수정 실패 - 중복된 사용자명: {}", newUsername);
            throw new UserNameAlreadyExistsException(newUsername);
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
            .map(profileRequest -> {

                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();

                log.debug("프로필 이미지 업데이트 - userId: {}, 파일명: {}, 크기: {} bytes",
                    userId, fileName, bytes.length);

                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes);
                return binaryContent;
            })
            .orElse(null);

        String newPassword = userUpdateRequest.newPassword();
        user.update(newUsername, newEmail, newPassword, nullableProfile);

        log.info("사용자 수정 완료 - userId: {}, username: {}, email: {}",
            userId, newUsername, newEmail);

        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        log.info("사용자 삭제 시작 - userId: {}", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("사용자 삭제 실패 - 존재하지 않는 ID: {}", userId);
            throw new UserNotFoundException(userId);
        }

        userRepository.deleteById(userId);

        log.info("사용자 삭제 완료 - userId: {}", userId);
    }
}
