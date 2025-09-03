package com.sprint.mission.discodeit.service.basic;

import static org.springframework.util.StringUtils.hasText;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final ApplicationEventPublisher eventPublisher;

    @CacheEvict(value = "users", allEntries = true)
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

    @Cacheable(value = "users")
    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAll() {
        log.info("[user] 전체 조회 요청 (cacheable) - 캐시 miss 시 DB 접근");

        List<UserDto> userDtos = userRepository.findAll()
            .stream()
            .map(userMapper::toDto)
            .toList();

        log.info("[user] 전체 조회 응답(DB 쿼리 실행됨): size={}", userDtos.size());
        return userDtos;
    }

    @CacheEvict(value = "users", allEntries = true)
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
        if (hasText(newEmail) && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(newEmail, user.getId())) {
                throw new DuplicateEmailException(newEmail);
            }
            user.updateEmail(newEmail);
        }

        if (hasText(newUsername) && !newUsername.equals(user.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(newUsername, user.getId())) {
                throw new DuplicateUserException(newUsername);
            }
            user.updateUsername(newUsername);
        }

        if (hasText(newPassword)) {
            user.updatePassword(passwordEncoder.encode(newPassword));
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

    @CacheEvict(value = "users", allEntries = true)
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
        userRepository.deleteById(userId);
        log.info("[user] 삭제 완료: id={}", userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public UserDto updateUserRole(RoleUpdateRequest request) {
        UUID userId = request.userId();
        Role newRole = request.newRole();
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        String username = user.getUsername();
        Role oldRole = user.getRole();

        log.info("[user] 사용자 권한 변경 요청: username={}, oldRole={}, newRole={}", username, oldRole,
            newRole);

        User updatedUser = user;

        if (user.getRole() != newRole) {
            user.updateRole(newRole);
            updatedUser = userRepository.save(user);
            log.info("[user] 사용자 권한 변경 완료: username={}", username);

            // 권한 변경 알림 이벤트 발행
            publishRoleUpdatedEvent(user, oldRole, newRole);
        } else {
            log.info("[user] 권한 변경 없음 - 기존과 동일한 권한: username={}, role={}", username, oldRole);
        }

        log.info("[user] 사용자 권한 변경 처리 완료");
        return userMapper.toDto(updatedUser);
    }

    // 변경 요청에 포함된 필드만 추출하여 로그 메시지 생성
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
            logMessage.append(", newPassword=***");
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
        return profileRequest.map(req -> {
            BinaryContent meta = new BinaryContent(
                req.fileName(),
                req.size(),
                req.contentType()
            );
            BinaryContent saved = binaryContentRepository.save(meta);

            // Dto 변환 후 이벤트 발행
            BinaryContentDto dto = new BinaryContentDto(
                saved.getId(), saved.getFileName(), saved.getSize(), saved.getContentType(),
                saved.getStatus()
            );
            String objectKey = saved.getId().toString();
            eventPublisher.publishEvent(new BinaryContentCreatedEvent(
                dto,
                objectKey,
                req.toInputStreamSupplier()
            ));

            return saved;
        }).orElse(null);
    }

    // 권한 변경시 알림 이벤트 발행
    private void publishRoleUpdatedEvent(User user, Role oldRole, Role newRole) {
        eventPublisher.publishEvent(new RoleUpdatedEvent(
            user.getId(),
            oldRole,
            newRole,
            Instant.now()
        ));
    }
}
