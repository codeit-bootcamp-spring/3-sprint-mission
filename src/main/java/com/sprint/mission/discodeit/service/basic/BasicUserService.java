package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
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
    private final BinaryContentRepository binaryContentRepository;

    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    public UserDto create(UserCreateRequest request,
        Optional<BinaryContentCreateRequest> profileRequest) {
        String email = request.email();
        String username = request.username();
        String password = request.password();
        log.info("[user] 생성 요청: email={}, username={}", email, username);

        // email/username 중복 체크
        if (userRepository.existsByEmail(email)) {
            log.warn("[user] 생성 실패 - email 중복됨: email={}", email);
            throw new DuplicateEmailException(email);
        }
        if (userRepository.existsByUsername(username)) {
            log.warn("[user] 생성 실패 - username 중복됨: name={}", username);
            throw new DuplicateUserException(username);
        }

        // 프로필 이미지 생성
        BinaryContent nullableProfile = createProfile(profileRequest);

        User user = new User(username, email, password, nullableProfile);
        userRepository.save(user);

        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user, now);
        userStatusRepository.save(userStatus);
        log.info("[user] 생성 완료: userId={}, name={}, email={}, isProfile={}",
            user.getId(), username, email, nullableProfile != null);

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto find(UUID userId) {
        log.info("[user] 조회 요청: id={}", userId);

        return userRepository.findById(userId)
            .map(userMapper::toDto)
            .orElseThrow(() -> {
                log.warn("[user] 조회 실패 - 존재하지 않는 id: id={}", userId);
                return new UserNotFoundException(userId);
            });
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAll() {
        log.info("[user] 전체 조회 요청");

        List<UserDto> userDtos = userRepository.findAll()
            .stream()
            .map(userMapper::toDto)
            .toList();

        log.info("[user] 전체 조회 응답: size={}", userDtos.size());
        return userDtos;
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> profileRequest) {
        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();

        // update시 요청된 값만 로그 출력
        String logMessage = makeUpdateLog(userId, userUpdateRequest, profileRequest);
        log.info("[user] 수정 요청: {}", logMessage);

        // 1. 수정할 엔티티 조회
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("[user] 수정 실패 - 존재하지 않는 id: id={}", userId);
            return new UserNotFoundException(userId);
        });

        // 2. email/username 중복 체크
        if (userRepository.existsByEmail(newEmail)) {
            log.warn("[user] 수정 실패 - email 중복됨: email={}", newEmail);
            throw new DuplicateEmailException(newEmail);
        }
        if (userRepository.existsByUsername(newUsername)) {
            log.warn("[user] 수정 실패 - username 중복됨: name={}", newUsername);
            throw new DuplicateUserException(newUsername);
        }

        // 3. 프로필이미지 있으면 지우고, 생성
        if (user.getProfile() != null) {
            binaryContentRepository.delete(user.getProfile());
        }
        BinaryContent newNullableProfile = createProfile(profileRequest);

        user.update(newUsername, newEmail, newPassword, newNullableProfile);
        log.info("[user] 수정 완료: {}", logMessage);

        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
                log.warn("[user] 삭제 실패 - 존재하지 않는 id: id={}", userId);
                return new UserNotFoundException(userId);
            }
        );
        Optional.ofNullable(user.getProfile()).ifPresent(binaryContentRepository::delete);
        userStatusRepository.deleteByUserId(userId);

        userRepository.deleteById(userId);
        log.info("[user] 삭제 완료: id={}", userId);
    }

    /**
     * 변경 요청에 포함된 필드만 추출하여 로그 메시지 생성
     * <p>
     * 비밀번호는 실제 값 대신 마스킹, 프로필 이미지 변경 여부는 boolean 형태
     *
     * @param userId     업데이트 대상 사용자의 식별자
     * @param request    사용자 정보 수정 요청 객체
     * @param newProfile 새로운 프로필 이미지 요청 (Optional)
     * @return 변경된 필드 포함한 로그 메시지 문자열
     */
    private String makeUpdateLog(UUID userId, UserUpdateRequest request,
        Optional<BinaryContentCreateRequest> newProfile) {
        StringBuilder logMessage = new StringBuilder("userId=" + userId);

        String newUsername = request.newUsername();
        String newEmail = request.newEmail();
        String newPassword = request.newPassword();

        if (newUsername != null) {
            logMessage.append(", newUsername=").append(newUsername);
        }
        if (newEmail != null) {
            logMessage.append(", newEmail=").append(newEmail);
        }
        if (newPassword != null) {
            logMessage.append(", newPassword=******");
        }
        if (newProfile.isPresent()) {
            logMessage.append(", newProfileImage=true");
        }

        return logMessage.toString();
    }

    /**
     * 프로필이미지 생성 메서드
     */
    private BinaryContent createProfile(Optional<BinaryContentCreateRequest> profileRequest) {
        return profileRequest.map(profileCreateRequest -> {
                byte[] bytes = profileCreateRequest.bytes();

                BinaryContent binaryContent = new BinaryContent(
                    profileCreateRequest.fileName(),
                    (long) profileCreateRequest.bytes().length,
                    profileCreateRequest.contentType()
                );

                binaryContentRepository.save(binaryContent);
                // Storage에 bytes 저장
                binaryContentStorage.put(binaryContent.getId(), bytes);
                return binaryContent;
            })
            .orElse(null);
    }
}
