package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;
    //
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserStatusRepository userStatusRepository;

    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.debug("사용자 생성 시작: {}", userCreateRequest);

        // 중복 유저 확인
        validateUserUniqueness(userCreateRequest.email(), userCreateRequest.username());

        // 프로필 처리
        BinaryContent nullableProfile = createProfile(optionalProfileCreateRequest);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userCreateRequest.password());

        // 유저 생성 및 저장
        User user = new User(userCreateRequest.username(), userCreateRequest.email(), encodedPassword, nullableProfile);
        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user, now);

        userRepository.save(user);

        log.info("사용자 생성 완료: id={}, username={}", user.getId(), user.getUsername());

        return userMapper.toDto(user);
    }

    private void validateUserUniqueness(String email, String username) {
        if (userRepository.existsByEmail(email)) {
            throw UserAlreadyExistsException.withEmail(email);
        }
        if (userRepository.existsByUsername(username)) {
            throw UserAlreadyExistsException.withUsername(username);
        }
    }

    private BinaryContent createProfile(Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        return optionalProfileCreateRequest
                .map(profileRequest -> {
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long)bytes.length, contentType);
                    binaryContentRepository.save(binaryContent);
                    binaryContentStorage.put(binaryContent.getId(), bytes);
                    log.info("BinaryContent creation complete: id={}, fileName={}, size={}",
                        binaryContent.getId(), fileName, bytes.length);
                    return binaryContent;
                })
                .orElse(null);
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.debug("사용자 수정 시작: id={}, request={}", userId, userUpdateRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    UserNotFoundException exception = UserNotFoundException.withId(userId);
                    return exception;
                });

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        validateUserUniqueness(newEmail, newUsername);

        BinaryContent nullableProfile = optionalProfileCreateRequest
                .map(profileRequest -> {

                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
                    return binaryContent;
                })
                .orElse(null);

        String newPassword = userUpdateRequest.newPassword();
        String encodedPassword = Optional.ofNullable(newPassword).map(passwordEncoder::encode)
            .orElse(user.getPassword());
        user.update(newUsername, newEmail, encodedPassword, nullableProfile);

        log.info("사용자 수정 완료: id={}", userId);

        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        log.debug("사용자 삭제 시작: id={}", userId);

        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException.withId(userId);
        }

        userRepository.deleteById(userId);

        log.info("사용자 삭제 완료: id={}", userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateRole(RoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.withId(userId));

        user.updateRole(request.newRole());

        sessionRegistry.getAllPrincipals().stream()
            .filter(principal -> ((DiscodeitUserDetails) principal).getUserDto().id().equals(userId))
            .findFirst()
            .ifPresent(principal -> {
                    List<SessionInformation> activeSessions = sessionRegistry.getAllSessions(principal,
                        false);
                    log.debug("Active sessions: {}", activeSessions.size());
                    activeSessions.forEach(SessionInformation::expireNow);
                }
            );

        return userMapper.toDto(user);
    }

    private void invalidateUserSessions(String username) {
        try {
            // SessionRegistry에서 모든 주체(Principal) 조회
            List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
            System.out.println("[UserService] 전체 로그인된 사용자 수: " + allPrincipals.size());

            // 해당 사용자의 모든 세션 정보 찾기
            for (Object principal : allPrincipals) {

                /* 설명. 해당 프로젝트에서는 Principal이 항상 UserDetails 구현체(CustomUserDetails)임.
                 *  참고로 다른 프로젝트의 경우, principal이 UserDetails 구현체가 아닐 수 있다.
                 * */
                UserDetails userDetails = (UserDetails) principal;
                String principalName = userDetails.getUsername();

                if (username.equals(principalName)) {

                    // 해당 사용자의 모든 세션 정보 가져오기
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                    System.out.println("[UserService] 대상 사용자 발견! 활성 세션 수: " + sessions.size());

                    // 모든 세션 무효화
                    for (SessionInformation session : sessions) {
                        System.out.println("[UserService] 세션 무효화 중 - 세션ID: " + session.getSessionId());
                        session.expireNow();
                        System.out.println("[UserService] 세션 무효화 완료 - 만료됨: " + session.isExpired());
                    }

                    System.out.println("[UserService] 사용자 '" + username + "'의 모든 세션(" + sessions.size() + "개)이 무효화되었습니다.");
                    break;
                }
            }

            System.out.println("[UserService] ========== 세션 무효화 완료 ==========");

        } catch (Exception e) {
            System.err.println("[UserService] 세션 무효화 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            // 세션 무효화 실패는 권한 변경 자체를 실패시키지 않음 (DB 변경은 유지)
        }
    }
}
