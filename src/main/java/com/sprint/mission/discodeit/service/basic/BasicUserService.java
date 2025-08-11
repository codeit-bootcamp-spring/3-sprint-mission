package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.SessionUtils;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final SessionUtils sessionUtils;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.info("사용자 생성 요청: username={}, email={}", userCreateRequest.username(),
            userCreateRequest.email());

        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistException("email", email);
        }
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistException("username", username);
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
            .map(profileRequest -> {
                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes, contentType);
                log.debug("프로필 이미지 저장 완료: id={}", binaryContent.getId());
                return binaryContent;
            })
            .orElse(null);

        String password = userCreateRequest.password();
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, email, encodedPassword, nullableProfile);

        userRepository.save(user);
        log.info("사용자 생성 완료: id={}", user.getId());
        return userMapper.toDto(user, sessionUtils);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto find(UUID userId) {
        log.debug("사용자 조회 요청: id={}", userId);

        return userRepository.findById(userId)
            .map(user -> userMapper.toDto(user, sessionUtils))
            .orElseThrow(() -> {
                log.warn("사용자 조회 실패: id={}", userId);
                return new UserNotFoundException(userId);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        log.debug("모든 사용자 조회 요청");
        return userRepository.findAll().stream()
            .map(user -> userMapper.toDto(user, sessionUtils))
            .toList();
    }

    @Transactional
    @Override

    @PreAuthorize("#userId == principal.id")
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        log.info("사용자 수정 요청: id={}, newUsername={}, newEmail={}", userId,
            userUpdateRequest.newUsername(), userUpdateRequest.newEmail());

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.warn("사용자 수정 실패: 존재하지 않는 id={}", userId);
                return new UserNotFoundException(userId);
            });

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        if (userRepository.existsByEmail(newEmail)) {
            log.error("이메일 중복(수정): {}", newEmail);
            throw new UserAlreadyExistException("email", newEmail);
        }
        if (userRepository.existsByUsername(newUsername)) {
            log.error("사용자명 중복(수정): {}", newUsername);
            throw new UserAlreadyExistException("username", newUsername);
        }

        BinaryContent nullableProfile = optionalProfileCreateRequest
            .map(profileRequest -> {
                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), bytes, contentType);
                log.debug("프로필 이미지 수정 완료: id={}", binaryContent.getId());
                return binaryContent;
            })
            .orElse(null);

        String newPassword = userUpdateRequest.newPassword();
        user.update(newUsername, newEmail, newPassword, nullableProfile);

        log.info("사용자 수정 완료: id={}", userId);
        return userMapper.toDto(user, sessionUtils);
    }

    @Transactional
    @Override
    @PreAuthorize("#userId == principal.id")
    public void delete(UUID userId) {
        log.info("사용자 삭제 요청: id={}", userId);

        if (!userRepository.existsById(userId)) {
            log.error("삭제 실패: 존재하지 않는 사용자 id={}", userId);
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);
        log.info("사용자 삭제 완료: id={}", userId);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateRole(UUID userId, Role newRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        user.updateRole(newRole);
        log.info("사용자 권한 변경: id={}, newRole={}", userId, newRole);

        expireSessionsForUsername(user.getUsername());

        return userMapper.toDto(user, sessionUtils);
    }

    private void expireSessionsForUsername(String username) {
        log.info("세션 무효화 시작");

        sessionRegistry.getAllPrincipals().stream()
            .filter(principal -> principal instanceof DiscodeitUserDetails)
            .map(DiscodeitUserDetails.class::cast)
            .filter(details -> details.getUsername().equals(username))
            .flatMap(details -> sessionRegistry.getAllSessions(details, false).stream())
            .forEach(sessionInformation -> {
                sessionInformation.expireNow();
                log.info("세션 만료 처리: sessionId={}", sessionInformation.getSessionId());
            });
    }
}