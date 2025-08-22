package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.service.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.auth.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateNameException;
import com.sprint.mission.discodeit.exception.user.NotFoundUserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.struct.BinaryContentStructMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("basicUserService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final BinaryContentStructMapper binaryContentMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtRegistry jwtRegistry;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto userRequestDto,
        BinaryContentDto binaryContentDto) {
        String username = userRequestDto.username();
        String email = userRequestDto.email();

        String currentThread = Thread.currentThread().getName();

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
            .build();

        // 회원가입 시 기본 권한은 USER
        user.updateRole(Role.USER);

        // 프로필 이미지를 등록한 경우
        if (binaryContentDto != null) {
            byte[] data = binaryContentDto.bytes();

            BinaryContent profileImage = binaryContentMapper.toEntity(binaryContentDto);

            user.updateProfile(profileImage);

            BinaryContent savedProfile = binaryContentRepository.save(profileImage);
            // 저장 후 이벤트 발행
            log.info("[BasicUserService] 유저 등록 프로필 메타데이터 저장 이벤트 발행 시작 - Thread : {}",
                currentThread);
            BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(savedProfile, data);
            eventPublisher.publishEvent(event);
            log.info("[BasicUserService] 유저 등록 프로필 메타데이터 저장 이벤트 발행 완료 - Thread: {}", currentThread);
        }

        User savedUser = userRepository.save(user);

        log.info("[BasicUserService] 사용자 등록 성공 - id: {}, username: {}, email: {}",
            savedUser.getId(), username, email);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto findById(UUID id) {
        User user = findUser(id);

        return userMapper.toDto(user);
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<UserResponseDto> users = userRepository.findAll().stream()
            .map(userMapper::toDto)
            .toList();

        return users;
    }

    @Override
    @PreAuthorize("#id == authentication.principal.id")
    @Transactional
    public UserResponseDto update(UUID id, UserUpdateDto userUpdateDto,
        BinaryContentDto binaryContentDto) {

        String currentThread = Thread.currentThread().getName();
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
            byte[] data = binaryContentDto.bytes();

            BinaryContent profileImage = binaryContentMapper.toEntity(binaryContentDto);

            // 기존 프로필 이미지 제거
            if (profile != null) {
                binaryContentRepository.deleteById(profile.getId());
            }

            user.updateProfile(profileImage);

            BinaryContent updatedProfile = binaryContentRepository.save(profileImage);
            log.info("[BasicUserService] 유저 정보 변경 프로필 메타 데이터 저장 이벤트 발행 시작 - Thread : {}",
                currentThread);
            BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(updatedProfile, data);
            eventPublisher.publishEvent(event);
            log.info("[BasicUserService] 유저 정보 변경 프로필 메타 데이터 저장 이벤트 발행 완료 - Thread: {}",
                currentThread);
        } else if (profile != null) {
            binaryContentRepository.deleteById(profile.getId());
            user.updateProfile(null);
        }

        Optional.ofNullable(userUpdateDto.newPassword()).ifPresent(user::updatePassword);

        User updatedUser = userRepository.save(user);
        UserResponseDto updatedUserDto = userMapper.toDto(updatedUser);

        // 사용자 정보 기반으로 새 UserDetails 생성
        DiscodeitUserDetails newUserDetails = new DiscodeitUserDetails(updatedUserDto,
            updatedUser.getPassword());

        // 인증 정보 갱신
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
            newUserDetails,
            null,
            newUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        log.info("[BasicUserService] 사용자 수정 성공! id: {}, username: {}, email: {}",
            updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail());

        return updatedUserDto;
    }

    @Override
    @PreAuthorize("#id == authentication.principal.id")
    @Transactional
    public void deleteById(UUID id) {
        log.info("[BasicUserService] 사용자 삭제 요청: id: {}", id);

        User user = findUser(id);
        log.debug("[BasicUserService] 사용자 조회 완료- id: {}, username: {}", user.getId(),
            user.getUsername());

        userRepository.deleteById(id);
        log.debug("[BasicUserService] userRepository 삭제 완료 - userId: {}", id);

        if (user.getProfile() != null) {
            binaryContentRepository.deleteById(user.getProfile().getId());
        }

        log.info("[BasicUserService] 사용자 삭제 완료 - userId: {}", id);
    }

    @Override
    @Transactional
    public UserResponseDto updateRole(RoleUpdateRequest request) {
        User user = findUser(request.userId());

        log.debug("[BasicUserService] 사용자: {}", user);

        user.updateRole(request.newRole());
        User updatedUser = userRepository.save(user);

        jwtRegistry.invalidateJwtInformationByUserId(user.getId());

        log.info("[BasicUserService] 사용자 권한 변경 완료: {}", updatedUser);

        return userMapper.toDto(user);
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new NotFoundUserException(id));
    }
}
