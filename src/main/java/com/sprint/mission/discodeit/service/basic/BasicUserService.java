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
import com.sprint.mission.discodeit.exception.user.DuplicatedUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

/**
 * 사용자(User) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * <p>사용자 생성, 수정, 삭제, 조회 기능을 제공합니다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
@ComponentScan(basePackages = "com.example.mapper")
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;
    private static final String SERVICE_NAME = "[UserService] ";

    /**
     * 신규 유저를 생성합니다.
     * @param userCreateRequest 유저 생성 요청 정보
     * @param optionalProfileCreateRequest 프로필 이미지 생성 요청(Optional)
     * @return 생성된 유저 DTO
     */
    @Override
    @Transactional
    public UserDto create(UserCreateRequest userCreateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();
        log.info(SERVICE_NAME + "신규 유저 생성 시도: username={}, email={}", username, email);

        if (userRepository.existsByEmail(email)) {
            log.error(SERVICE_NAME + "이미 존재하는 이메일: {}", email);
            throw new DuplicatedUserException("이미 가입된 이메일입니다.");
        }
        if (userRepository.existsByUsername(username)) {
            log.error(SERVICE_NAME + "이미 존재하는 사용자명: {}", username);
            throw new DuplicatedUserException("이미 가입된 사용자명입니다.");
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
            .map(profileRequest -> {
                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();
                log.debug(SERVICE_NAME + "프로필 파일 저장: fileName={}, contentType={}, size={}", fileName, contentType, bytes.length);
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent); // profile 기본 정보 저장
                binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes()); // profile 정보 저장
                return binaryContent;
            })
            .orElse(null);
        String password = userCreateRequest.password();

        User user = new User(username, email, password, nullableProfile);
        userRepository.saveAndFlush(user);

        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user, now);
        user.setStatus(userStatus);
        userStatusRepository.save(userStatus);

        log.info(SERVICE_NAME + "신규 유저 생성 성공: userId={}", user.getId());
        return userMapper.toDto(user);
    }

    /**
     * 특정 유저를 조회합니다.
     * @param userId 조회할 유저 ID
     * @return 조회된 유저 DTO
     */
    @Override
    public UserDto find(UUID userId) {
        log.info(SERVICE_NAME + "유저 조회 시도: userId={}", userId);
        return userRepository.findById(userId)
            .map(userMapper::toDto)
            .orElseThrow(() -> {
                log.error(SERVICE_NAME + "유저 없음: userId={}", userId);
                return new UserNotFoundException("해당 사용자를 찾을 수 없습니다.");
            });
    }

    /**
     * 전체 유저 목록을 조회합니다.
     * @return 유저 DTO 목록
     */
    @Override
    public List<UserDto> findAll() {
        log.info(SERVICE_NAME + "전체 유저 목록 조회 시도");
        List<UserDto> result = userRepository.findAllWithProfileAndStatus()
            .stream()
            .map(userMapper::toDto)
            .toList();
        log.info(SERVICE_NAME + "전체 유저 목록 조회 성공: 건수={}", result.size());
        return result;
    }

    /**
     * 유저 정보를 수정합니다.
     * @param userId 수정할 유저 ID
     * @param userUpdateRequest 유저 수정 요청 정보
     * @param optionalProfileCreateRequest 프로필 이미지 생성 요청(Optional)
     * @return 수정된 유저 DTO
     */
    @Override
    @Transactional
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.info(SERVICE_NAME + "유저 정보 수정 시도: userId={}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error(SERVICE_NAME + "유저 없음: userId={}", userId);
                return new UserNotFoundException("해당 사용자를 찾을 수 없습니다.");
            });

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();
        // username 업데이트 (null이 아닌 경우에만)
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            if (userRepository.existsByUsername(newUsername) && !newUsername.equals(user.getUsername())) {
                log.error(SERVICE_NAME + "이미 존재하는 사용자명(수정): {}", newUsername);
                throw new DuplicatedUserException("이미 가입된 사용자명입니다.");
            }
        } else {
            newUsername = user.getUsername();
        }
        // email 업데이트 (null이 아닌 경우에만)
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            if (userRepository.existsByEmail(newEmail) && !newEmail.equals(user.getEmail())) {
                log.error(SERVICE_NAME + "이미 존재하는 이메일(수정): {}", newEmail);
                throw new DuplicatedUserException("이미 가입된 이메일입니다.");
            }
        } else {
            newEmail = user.getEmail();
        }
        // password 업데이트 (null이 아닌 경우에만)
        if (newPassword == null || newPassword.trim().isEmpty()) {
            newPassword = user.getPassword();
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
            .map(profileRequest -> {
                BinaryContent existingProfile = user.getProfile();
                if (existingProfile != null && existingProfile.getId() != null) {
                    log.debug(SERVICE_NAME + "기존 프로필 파일 삭제: id={}", existingProfile.getId());
                    binaryContentRepository.deleteById(existingProfile.getId());
                }
                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();
                log.debug(SERVICE_NAME + "새 프로필 파일 저장: fileName={}, contentType={}, size={}", fileName, contentType, bytes.length);
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes);
                return binaryContent;
            })
            .orElse(user.getProfile());

        user.update(newUsername, newEmail, newPassword, nullableProfile);
        log.info(SERVICE_NAME + "유저 정보 수정 성공: userId={}", userId);
        return userMapper.toDto(user);
    }

    /**
     * 유저를 삭제합니다.
     * @param userId 삭제할 유저 ID
     */
    @Override
    @Transactional
    public void delete(UUID userId) {
        log.info(SERVICE_NAME + "유저 삭제 시도: userId={}", userId);
        if (!userRepository.existsById(userId)) {
            log.error(SERVICE_NAME + "유저 없음: userId={}", userId);
            throw new UserNotFoundException("해당 사용자를 찾을 수 없습니다.");
        }
        userRepository.deleteById(userId);
        log.info(SERVICE_NAME + "유저 삭제 성공: userId={}", userId);
    }
}
