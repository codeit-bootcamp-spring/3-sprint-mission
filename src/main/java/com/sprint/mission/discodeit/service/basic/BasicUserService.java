package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateNameException;
import com.sprint.mission.discodeit.exception.user.NotFoundUserException;
import com.sprint.mission.discodeit.exception.userstatus.NotFoundUserStatusException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.struct.BinaryContentStructMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service("basicUserService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper userMapper;
    private final BinaryContentStructMapper binaryContentMapper;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto userRequestDto,
        BinaryContentDto binaryContentDto) {
        String username = userRequestDto.username();
        String email = userRequestDto.email();

        log.info("[BasicUserService] 사용자 등록 요청 - username: {}, email: {}", username, email);

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateNameException(username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userRequestDto.password());

        User user = User.builder()
            .username(username)
            .email(email)
            .password(encodedPassword)
            .profile(null)
            .status(null)
            .build();

        // 회원가입 시 기본 권한은 USER
        user.updateRole(Role.USER);

        // 프로필 이미지를 등록한 경우
        if (binaryContentDto != null) {
            byte[] bytes = binaryContentDto.bytes();

            BinaryContent profileImage = binaryContentMapper.toEntity(binaryContentDto);

            user.updateProfile(profileImage);

            binaryContentRepository.save(profileImage);
            binaryContentStorage.put(profileImage.getId(), bytes);
        }

        UserStatus userStatus = UserStatus.builder()
            .user(user)
            .lastActiveAt(Instant.now())
            .build();

        user.updateStatus(userStatus);

        User savedUser = userRepository.save(user);
        userStatusRepository.save(userStatus);

        log.info("[BasicUserService] 사용자 등록 성공 - id: {}, username: {}, email: {}",
            savedUser.getId(), username, email);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto findById(UUID id) {
        User user = findUser(id);

        UserStatus userStatus = findUserStatus(id);

        // 마지막 접속 시간 확인
        user.updateStatus(userStatus);

        return userMapper.toDto(user);
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<UserResponseDto> users = userRepository.findAll().stream()
            .map(user -> {
                UserStatus userStatus = findUserStatus(user.getId());
                user.updateStatus(userStatus);
                return userMapper.toDto(user);
            })
            .toList();

        return users;
    }

    @Override
    @Transactional
    public UserResponseDto update(UUID id, UserUpdateDto userUpdateDto,
        BinaryContentDto binaryContentDto) {
        User user = findUser(id);

        String newUsername = userUpdateDto.newUsername();
        String newEmail = userUpdateDto.newEmail();

        log.info("[BasicUserService] 사용자 수정 요청: id: {}, newUsername: {}, newEmail: {}",
            id, newUsername, newEmail);

        if (newUsername != null) {
            userRepository.findByUsername(newUsername)
                .filter(u -> !u.getId().equals(user.getId()))
                .ifPresent(u -> {
                    throw new DuplicateNameException(newUsername);
                });
            user.updateName(newUsername);
        }

        if (newEmail != null) {
            userRepository.findByEmail(newEmail)
                .filter(u -> !u.getId().equals(user.getId()))
                .ifPresent(u -> {
                    throw new DuplicateEmailException(newEmail);
                });
            user.updateEmail(newEmail);
        }

        // 프로필 이미지 처리
        BinaryContent profile = user.getProfile();
        if (binaryContentDto != null) {
            byte[] bytes = binaryContentDto.bytes();

            BinaryContent profileImage = binaryContentMapper.toEntity(binaryContentDto);

            // 기존 프로필 이미지 제거
            if (profile != null) {
                binaryContentRepository.deleteById(profile.getId());
            }

            user.updateProfile(profileImage);

            binaryContentRepository.save(profileImage);
            binaryContentStorage.put(profileImage.getId(), bytes);
        } else if (profile != null) {
            binaryContentRepository.deleteById(profile.getId());
            user.updateProfile(null);
        }

        Optional.ofNullable(userUpdateDto.newPassword()).ifPresent(user::updatePassword);

        User updatedUser = userRepository.save(user);

        log.info("[BasicUserService] 사용자 수정 성공! id: {}, username: {}, email: {}",
            updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail());

        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("[BasicUserService] 사용자 삭제 요청: id: {}", id);

        User user = findUser(id);
        log.debug("[BasicUserService] 사용자 조회 완료- id: {}, username: {}", user.getId(),
            user.getUsername());

        userRepository.deleteById(id);
        log.debug("[BasicUserService] userRepository 삭제 완료 - userId: {}", id);

        userStatusRepository.deleteByUserId(id);

        if (user.getProfile() != null) {
            binaryContentRepository.deleteById(user.getProfile().getId());
        }

        log.info("[BasicUserService] 사용자 삭제 완료 - userId: {}", id);
    }

    @Override
    public UserResponseDto updateRole(RoleUpdateRequest request) {
        User user = findUser(request.userId());

        String username = user.getUsername();

        log.debug("[BasicUserService] 사용자: {}", user);

        user.updateRole(request.newRole());
        User updatedUser = userRepository.save(user);

        // 권한 변경 후 해당 유저의 모든 세션 무효화
        invalidateUserSessions(username);

        log.info("[BasicUserService] 사용자 권한 변경 완료: {}", updatedUser);

        return userMapper.toDto(user);
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundUserException(id));
    }

    private UserStatus findUserStatus(UUID id) {
        return userStatusRepository.findByUserId(id)
            .orElseThrow(() -> new NotFoundUserStatusException(id));
    }

    /**
     * 특정 사용자의 모든 세션 무효화 권한 변경 시 호출
     *
     * @param username 세션을 무효화할 사용자명
     */
    private void invalidateUserSessions(String username) {

        try {
            log.debug("[BasicUserService] 세션 무효화 대상 User: {}", username);

            // 모든 주체(principal) 조회
            List<Object> principals = sessionRegistry.getAllPrincipals();
            log.debug("[BasicUserService] 전체 로그인 된 사용자 수: {}", principals.size());

            // 해당 사용자의 모든 세션 찾기
            for (Object principal : principals) {
                UserDetails userDetails = (UserDetails) principal;
                String principalName = userDetails.getUsername();

                if (username.equals(principalName)) {

                    // 해당 사용자의 모든 세션 가져오기
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal,
                        false);
                    log.debug("[BasicUserService] 해당 사용자의 활성 세션 수: {}", sessions.size());

                    for (SessionInformation session : sessions) {
                        log.debug("[BasicUserService] 세션 {} 무효화 중", session.getSessionId());
                        session.expireNow();
                        log.debug("[BasicUserService] 세션 무효화 완료 ID: {}", session.getSessionId());
                    }

                    break;
                }
            }
        } catch (Exception e) {
            log.error("[BasicUserService] 세션 무효화 중 오류 발생: {}", e.getMessage());
        }
    }
}
