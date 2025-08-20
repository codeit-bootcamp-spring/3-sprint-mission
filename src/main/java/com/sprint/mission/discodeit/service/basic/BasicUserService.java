package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;
    private final JdbcTemplate jdbcTemplate;
    private final UserDetailsService userDetailsService;

    @Transactional
    public UserDto create(UserCreateRequest request,
        Optional<BinaryContentCreateRequest> profileRequest) {
        String email = request.email();
        String username = request.username();
        String encodedPassword = passwordEncoder.encode(request.password());
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

        User user = new User(username, email, encodedPassword, nullableProfile);
        User savedUser = userRepository.save(user);

        log.info("[user] 생성 완료: userId={}, name={}, email={}, isProfile={}",
            user.getId(), username, email, nullableProfile != null);

        return userMapper.toDto(savedUser);
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

    @PreAuthorize("@userPermissionEvaluator.isSelf(#userId, authentication.principal.userDto.id)")
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
        BinaryContent newNullableProfile = null;
        if (profileRequest.isPresent()) {
            if (user.getProfile() != null) {
                binaryContentRepository.delete(user.getProfile());
            }
            newNullableProfile = createProfile(profileRequest);
            user.updateProfile(newNullableProfile);
        }

        // 4. 변경 감지
        if (newUsername != null && !newUsername.equals(user.getUsername())) {
            user.updateUsername(newUsername);
        }
        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            user.updateEmail(newEmail);
        }
        if (newPassword != null) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.updatePassword(encodedPassword);
        }
        user.updateProfile(newNullableProfile);
        log.info("[user] 수정 완료: {}", logMessage);

        String finalUsername = (newUsername != null) ? newUsername : user.getUsername();
        UserDetails updatedUserDetails = userDetailsService.loadUserByUsername(finalUsername);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
            updatedUserDetails,
            updatedUserDetails.getPassword(),
            updatedUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return userMapper.toDto(user);
    }

    @PreAuthorize("@userPermissionEvaluator.isSelf(#userId, authentication.principal.userDto.id)")
    @Transactional
    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
                log.warn("[user] 삭제 실패 - 존재하지 않는 id: id={}", userId);
                return new UserNotFoundException(userId);
            }
        );
        Optional.ofNullable(user.getProfile()).ifPresent(binaryContentRepository::delete);
        jdbcTemplate.update("DELETE FROM persistent_logins WHERE username = ?", user.getUsername());
        userRepository.deleteById(userId);
        log.info("[user] 삭제 완료: id={}", userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserDto updateUserRole(RoleUpdateRequest request) {
        UUID userId = request.userId();
        Role newRole = request.newRole();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        String username = user.getUsername();
        Role oldRole = user.getRole();

        log.info("[user] 사용자 권한 변경 요청: username={}, oldRole={}, newRole={}", username, oldRole, newRole);

        user.updateRole(newRole);
        User updatedUser = userRepository.save(user);

        // 권한이 변경된 사용자의 모든 활성 세션을 무효화
        invalidateUserSessions(username);

        log.info("[user] 사용자 권한 변경 완료 및 세션 무효화 처리됨");

        return userMapper.toDto(updatedUser);
    }

    // 변경 요청에 포함된 필드만 추출하여 로그 메시지 생성
    private String makeUpdateLog(UUID userId, UserUpdateRequest request,
        Optional<BinaryContentCreateRequest> newProfile) {
        StringBuilder logMessage = new StringBuilder("userId=" + userId);

        String newUsername = request.newUsername();
        String newEmail = request.newEmail();
        String newPassword = passwordEncoder.encode(request.newPassword());

        if (newUsername != null) {
            logMessage.append(", newUsername=").append(newUsername);
        }
        if (newEmail != null) {
            logMessage.append(", newEmail=").append(newEmail);
        }
        if (newPassword != null) {
            logMessage.append(", newPassword=").append(newPassword);
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

    /**
     * 특정 사용자의 모든 활성 세션을 무효화
     * <p>
     * 권한 변경, 비밀번호 변경 등 보안상 중요한 변경 시 호출
     *
     * @param username 세션을 무효화할 사용자명
     */
    private void invalidateUserSessions(String username) {
        try {
            log.info("[UserService] 세션 무효화 시작 - 대상 사용자: {}", username);

            // SessionRegistry에서 모든 주체(Principal) 조회
            List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
            log.debug("[UserService] 전체 로그인된 사용자 수: {}", allPrincipals.size());

            // 해당 사용자의 모든 세션 정보 찾기
            for (Object principal : allPrincipals) {
                if (!(principal instanceof UserDetails userDetails)) {
                    log.warn("[UserService] 예상치 못한 Principal 타입: {}", principal.getClass().getName());
                    continue;
                }

                String principalName = userDetails.getUsername();
                log.debug("[UserService] 확인 중인 Principal - username: {}", principalName);

                if (username.equals(principalName)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
                    log.info("[UserService] 대상 사용자 발견! 활성 세션 수: {}", sessions.size());

                    // 모든 세션 무효화
                    for (SessionInformation session : sessions) {
                        log.info("[UserService] 세션 무효화 중 - 세션ID: {}", session.getSessionId());
                        session.expireNow();
                        log.info("[UserService] 세션 무효화 완료 - 만료됨: {}", session.isExpired());
                    }

                    log.info("[UserService] 사용자 '{}'의 모든 세션({}개)이 무효화되었습니다.", username, sessions.size());
                    break;
                }
            }

            log.info("[UserService] 세션 무효화 작업 완료 - username: {}", username);

        } catch (Exception e) {
            log.error("[UserService] 세션 무효화 중 오류 발생 - username: {}, message: {}", username, e.getMessage(), e);
            // 세션 무효화 실패는 권한 변경(DB 반영)을 막지 않음
        }
    }
}
