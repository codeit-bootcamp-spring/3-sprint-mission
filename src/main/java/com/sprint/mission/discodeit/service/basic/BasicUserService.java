package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.auth.util.OnlineStatusUtil;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.UserUpdateEvent;
import com.sprint.mission.discodeit.event.UserUpdateType;
import com.sprint.mission.discodeit.exception.binarycontent.ResourceLoadFailedException;
import com.sprint.mission.discodeit.exception.user.UserEmailDuplicationException;
import com.sprint.mission.discodeit.exception.user.UserNameDuplicationException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final OnlineStatusUtil onlineStatusUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "usersAll", allEntries = true)
    public UserDto create(UserCreateRequest request,
            Optional<BinaryContentCreateRequest> optionalProfileImage) {

        // 출력으로 확인만 하고 싶은 로그
        log.info("BinaryContentStorage 구현체: {}", binaryContentStorage.getClass().getName());

        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.email())) {
            throw new UserEmailDuplicationException(request.email());
        }

        // 이름 중복 검사
        if (userRepository.existsByUsername(request.username())) {
            throw new UserNameDuplicationException(request.username());
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        // 유저 생성
        User user = new User(
                request.username(),
                request.email(),
                encodedPassword
        );

        // 영속성 컨테이너 등록
        User savedUser = userRepository.save(user);

        // 프로필 이미지 처리 (기본 이미지 or 전달된 이미지)
        BinaryContent profile = optionalProfileImage
                .map(profileImage -> {
                    BinaryContent content = new BinaryContent(
                            profileImage.fileName(),
                            (long) profileImage.bytes().length,
                            profileImage.contentType()
                    );

                    binaryContentRepository.save(content);

                    // 이벤트 객체를 생성
                    BinaryContentCreatedEvent binaryContentEvent = BinaryContentCreatedEvent.now(
                        content.getId(),
                        profileImage.bytes()
                    );
                    // 생성된 이벤트 객체를 전달인자로 이벤트 발행
                    eventPublisher.publishEvent(binaryContentEvent);

                    savedUser.updateProfile(content);

                    return content;
                })
                .orElseGet(() -> {
                    try {
                        ClassPathResource resource = new ClassPathResource(
                                "static/images/default-avatar.png");
                        byte[] data = resource.getInputStream().readAllBytes();
                        BinaryContent defaultContent = new BinaryContent("default-avatar.png",
                                (long) data.length, "image/png");
                        binaryContentRepository.save(defaultContent);

                        // 이벤트 객체를 생성
                        BinaryContentCreatedEvent binaryContentEvent = BinaryContentCreatedEvent.now(
                            defaultContent.getId(),
                            data
                        );
                        // 생성된 이벤트 객체를 전달인자로 이벤트 발행
                        eventPublisher.publishEvent(binaryContentEvent);

                        savedUser.updateProfile(defaultContent);

                        return defaultContent;
                    } catch (IOException e) {
                        throw new ResourceLoadFailedException("static/images/default-avatar.png", e);
                    }
                });

        // 로그인 여부 (온라인 여부)
        boolean isOnline = onlineStatusUtil.isOnlineUser(savedUser.getId());
        UserDto dto = userMapper.toDto(savedUser, isOnline);

        eventPublisher.publishEvent(new UserUpdateEvent(UserUpdateType.CREATED, dto));

        return dto;
    }

    // 아이디로 검색
    @Override
    @Transactional(readOnly = true)
    public UserDto find(UUID id) {
        //유저 조회
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id));

        boolean isOnline = onlineStatusUtil.isOnlineUser(user.getId());
        return userMapper.toDto(user, isOnline);
    }


    // 전체 유저 조회
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "usersAll")
    public List<UserDto> findAll() {

        return userRepository.findAllWithProfileAndStatus()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }


    // 유저 정보와 프로필 이미지 수정
    @PreAuthorize("#userId == authentication.principal.id")
    @Override
    @Transactional
    @CacheEvict(cacheNames = "usersAll", allEntries = true)
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
            Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserNotFoundException(userId));

        // 이메일 중복 검사
        if (userRepository.existsByEmail(userUpdateRequest.newEmail())) {
            throw new UserEmailDuplicationException(userUpdateRequest.newEmail());
        }

        // 이름 중복 검사
        if (userRepository.existsByUsername(userUpdateRequest.newUsername())) {
            throw new UserNameDuplicationException(userUpdateRequest.newUsername());
        }

        if ((userUpdateRequest.newEmail()) != null) {
            user.updateEmail(userUpdateRequest.newEmail());
        }

        if ((userUpdateRequest.newUsername()) != null) {
            user.updateName(userUpdateRequest.newUsername());
        }

        if ((userUpdateRequest.newPassword()) != null) {
            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(userUpdateRequest.newPassword());
            user.updatePassword(encodedPassword);
        }

        // 프로필 이미지 처리 (기본 이미지 or 전달된 이미지)
        BinaryContent savedBinary = optionalProfileCreateRequest
                .map(profileImage -> {
                    // 기존 이미지 있으면 삭제
                    Optional.ofNullable(user.getProfile())
                            .ifPresent(profile -> {
                                binaryContentRepository.deleteById(profile.getId());
                            });

                    // 새 이미지 등록
                    BinaryContent content = new BinaryContent(
                            profileImage.fileName(),
                            (long) profileImage.bytes().length,
                            profileImage.contentType()
                    );
                    binaryContentRepository.save(content);

                    // 이벤트 객체를 생성
                    BinaryContentCreatedEvent binaryContentEvent = BinaryContentCreatedEvent.now(
                        content.getId(),
                        profileImage.bytes()
                    );
                    // 생성된 이벤트 객체를 전달인자로 이벤트 발행
                    eventPublisher.publishEvent(binaryContentEvent);
                    
                    user.updateProfile(content);

                    return content;
                })
                .orElse(null);

        UserDto dto = userMapper.toDto(user, true);

        eventPublisher.publishEvent(new UserUpdateEvent(UserUpdateType.UPDATED, dto));

        return dto;
    }


    // 유저 삭제
    @PreAuthorize("#id == authentication.principal.id")
    @Override
    @Transactional
    @CacheEvict(cacheNames = "usersAll", allEntries = true)
    public void delete(UUID id) {
        // 유저 조회
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id));

        // 바이트 파일 삭제
        Optional.ofNullable(user.getProfile())
                .ifPresent(profile -> binaryContentStorage.delete(profile.getId()));

        UserDto dto = userMapper.toDto(user, true);

        eventPublisher.publishEvent(new UserUpdateEvent(UserUpdateType.UPDATED, dto));

        // 유저 삭제 - 영속성 전이로 UserStatus, BinaryContent 삭제
        userRepository.delete(user);
    }
}
