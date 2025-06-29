package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.binarycontent.ResourceLoadFailedException;
import com.sprint.mission.discodeit.exception.user.UserEmailDuplicationException;
import com.sprint.mission.discodeit.exception.user.UserNameDuplicationException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundByUserException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
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

    private static final Logger logger = Logger.getLogger(BasicUserService.class.getName());


    @Override
    @Transactional
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

        // 유저 생성
        User user = new User(
                request.username(),
                request.email(),
                request.password()
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

                    binaryContentStorage.put(content.getId(), profileImage.bytes());
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

                        binaryContentStorage.put(defaultContent.getId(), data);
                        savedUser.updateProfile(defaultContent);

                        return defaultContent;
                    } catch (IOException e) {
                        throw new ResourceLoadFailedException("static/images/default-avatar.png", e);
                    }
                });

        // 유저 활동 상태 생성
        UserStatus userStatus = new UserStatus(savedUser, Instant.now());
        savedUser.setStatus(userStatus);

        return userMapper.toDto(savedUser);
    }

    // 아이디로 검색
    @Override
    @Transactional(readOnly = true)
    public UserDto find(UUID id) {
        //유저 조회
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id));

        UserStatus userStatus = userStatusRepository.findUserStatusByUserId(user.getId())
                .orElseThrow(
                        () -> new UserStatusNotFoundByUserException(user.getId()));

        return userMapper.toDto(user);
    }


    // 전체 유저 조회
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {

        return userRepository.findAllWithProfileAndStatus()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }


    // 유저 정보와 프로필 이미지 수정
    @Override
    @Transactional
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
            user.updatePassword(userUpdateRequest.newPassword());
        }

        // 프로필 이미지 처리 (기본 이미지 or 전달된 이미지)
        BinaryContent savedBinary = optionalProfileCreateRequest
                .map(profileImage -> {
                    // 기존 이미지 있으면 삭제
                    Optional.ofNullable(user.getProfile())
                            .ifPresent(profile -> {
                                binaryContentStorage.delete(profile.getId());
                                binaryContentRepository.deleteById(profile.getId());
                            });

                    // 새 이미지 등록
                    BinaryContent content = new BinaryContent(
                            profileImage.fileName(),
                            (long) profileImage.bytes().length,
                            profileImage.contentType()
                    );
                    binaryContentRepository.save(content);
                    binaryContentStorage.put(content.getId(), profileImage.bytes());
                    user.updateProfile(content);

                    return content;
                })
                .orElse(null);

        return userMapper.toDto(user);
    }


    // 유저 삭제
    @Override
    @Transactional
    public void delete(UUID id) {
        // 유저 조회
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(id));

        // 바이트 파일 삭제
        Optional.ofNullable(user.getProfile())
                .ifPresent(profile -> binaryContentStorage.delete(profile.getId()));

        // 유저 삭제 - 영속성 전이로 UserStatus, BinaryContent 삭제
        userRepository.delete(user);
    }
}
