package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.user.DuplicatedUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.web.sse.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 사용자(User) 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * 
 * <p>사용자 생성, 수정, 삭제, 조회 기능을 제공하며, 프로필 이미지 관리와
 * JWT 토큰 무효화 등의 보안 기능도 포함합니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>사용자 계정 생성 및 관리</li>
 *   <li>프로필 이미지 업로드 및 관리</li>
 *   <li>사용자 정보 수정 및 삭제</li>
 *   <li>JWT 토큰 무효화</li>
 *   <li>이벤트 기반 파일 처리</li>
 * </ul>
 * 
 * @author HuInDoL
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@Service
@ComponentScan(basePackages = "com.example.mapper")
public class BasicUserService implements UserService {

    private static final String SERVICE_NAME = "[UserService] ";

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final JwtRegistry jwtRegistry;
    private final ApplicationEventPublisher eventPublisher;
    private final SseService sseService;

    /**
     * 신규 유저를 생성합니다.
     * @param userCreateRequest 유저 생성 요청 정보
     * @param optionalProfileCreateRequest 프로필 이미지 생성 요청(Optional)
     * @return 생성된 유저 DTO
     */
    @Override
    @Transactional
    @CachePut(value = "users", key = "#result.id()")
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

                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent); // profile 기본 정보 저장
                log.debug(SERVICE_NAME + "프로필 파일 저장: fileName={}, contentType={}, size={}", fileName, contentType, bytes.length);

                log.debug(SERVICE_NAME + "BinaryContent 생성 이벤트 발행");
                BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContent, bytes, Instant.now());
                eventPublisher.publishEvent(event);

                return binaryContent;
            })
            .orElse(null);

        String rawPassword = userCreateRequest.password();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        log.debug(SERVICE_NAME + "비밀번호 암호화 완료");

        User user = new User(username, email, encodedPassword, nullableProfile);
        userRepository.saveAndFlush(user);

        log.info(SERVICE_NAME + "신규 유저 생성 성공: userId={}", user.getId());

        UserDto userDto = userMapper.toDto(user);
        sseAfterCommitBroadcast("users.created", userDto);

        return userDto;
    }

    /**
     * 특정 유저를 조회합니다.
     * @param userId 조회할 유저 ID
     * @return 조회된 유저 DTO
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userById", key = "#userId")
    public UserDto find(UUID userId) {
        log.info(SERVICE_NAME + "유저 조회 시도: userId={}", userId);
        return userRepository.findById(userId)
            .map(userMapper::toDto)
            .map(userDto -> UserDto.withOnlineStatus(userDto, isOnline(userDto.id())))
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
    @Transactional(readOnly = true)
    @Cacheable(value = "users")
    public List<UserDto> findAll() {
        log.info(SERVICE_NAME + "전체 유저 목록 조회 시도");
        List<UserDto> result = userRepository.findAllWithProfile()
            .stream()
            .map(userMapper::toDto)
            .map(userDto -> UserDto.withOnlineStatus(userDto, isOnline(userDto.id())))
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
    @CachePut(value = "users", key = "#result.id()")
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
        String rawPassword = userUpdateRequest.newPassword();

        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            rawPassword = user.getPassword();
        }
        String newPassword = passwordEncoder.encode(rawPassword);

        if (newUsername != null && !newUsername.trim().isEmpty()) {
            if (userRepository.existsByUsername(newUsername) && !newUsername.equals(user.getUsername())) {
                log.error(SERVICE_NAME + "이미 존재하는 사용자명(수정): {}", newUsername);
                throw new DuplicatedUserException("이미 가입된 사용자명입니다.");
            }
        } else {
            newUsername = user.getUsername();
        }

        if (newEmail != null && !newEmail.trim().isEmpty()) {
            if (userRepository.existsByEmail(newEmail) && !newEmail.equals(user.getEmail())) {
                log.error(SERVICE_NAME + "이미 존재하는 이메일(수정): {}", newEmail);
                throw new DuplicatedUserException("이미 가입된 이메일입니다.");
            }
        } else {
            newEmail = user.getEmail();
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
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                    contentType);
                binaryContentRepository.save(binaryContent);
                log.debug(SERVICE_NAME + "새 프로필 파일 저장: fileName={}, contentType={}, size={}", fileName, contentType, bytes.length);

                log.debug(SERVICE_NAME + "BinaryContent 수정 이벤트 발행");
                BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContent, bytes, Instant.now());
                eventPublisher.publishEvent(event);

                return binaryContent;
            })
            .orElse(user.getProfile());

        user.update(newUsername, newEmail, newPassword, nullableProfile);
        log.info(SERVICE_NAME + "유저 정보 수정 성공: userId={}", userId);

        UserDto userDto = UserDto.withOnlineStatus(userMapper.toDto(user), isOnline(user.getId()));
        log.info(SERVICE_NAME + "유저 접속 상태 반영 시작");

        sseAfterCommitBroadcast("users.updated", userDto);

        return userDto;
    }

    /**
     * 유저를 삭제합니다.
     * @param userId 삭제할 유저 ID
     */
    @Override
    @Transactional
    @CacheEvict(value = {"userById", "users"}, key = "#userId")
    public void delete(UUID userId) {
        log.info(SERVICE_NAME + "유저 삭제 시도: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        userRepository.deleteById(userId);
        log.info(SERVICE_NAME + "유저 삭제 성공: userId={}", userId);

        UserDto userDto = userMapper.toDto(user);
        sseAfterCommitBroadcast("users.deleted", userDto);
    }

    private boolean isOnline(UUID userId) {
        return jwtRegistry.hasActiveJwtInformationByUserId(userId);
    }

    @Override
    public boolean isUserOwner(UUID targetUserId, UUID currentUserId) {
        return targetUserId.equals(currentUserId);
    }

    private void sseAfterCommitBroadcast(String eventName, Object dto) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() { sseService.broadcast(eventName, dto); }
            });
        } else {
            sseService.broadcast(eventName, dto);
        }
    }
}
