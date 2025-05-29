package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserFindRequest;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public UserDto create(UserCreateRequest request,
                          Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
        validateUniqueUser(request);

        BinaryContent profile = binaryContentCreateRequest
                .map(this::storeBinaryContentMeta)
                .orElse(null);

        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                profile,
                null
        );
        User savedUser = userRepository.save(user);

        UserStatus status = new UserStatus(savedUser, Instant.now());
        userStatusRepository.save(status);

        return userMapper.toDto(savedUser);
    }

    /**
     * 파일 데이터를 스토리지에 저장하고, 메타 정보만 담은 BinaryContent 엔티티를 반환
     */
    private BinaryContent storeBinaryContentMeta(BinaryContentCreateRequest request) {
        UUID fileId;
        try {
            // 실제 파일 저장: storage.put(id=null -> 새 UUID 생성, bytes)
            fileId = binaryContentStorage.put(null, request.bytes());
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }

        // 메타 정보만 담아서 엔티티로 저장
        BinaryContent meta = new BinaryContent();
        meta.setId(fileId);
        meta.setFileName(request.fileName());
        meta.setSize((long) request.bytes().length);
        meta.setContentType(request.contentType());
        return binaryContentRepository.save(meta);
    }

    private void validateUniqueUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    @Override
    public UserDto find(UserFindRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저는 없습니다."));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto update(UUID userId, UserUpdateRequest request,
                          Optional<BinaryContentCreateRequest> profileRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저는 없습니다."));

        if (profileRequest.isPresent()) {
            // 기존 프로필이 있으면 삭제
            Optional.ofNullable(user.getProfile())
                    .ifPresent(binaryContentRepository::delete);

            // 새 파일 저장 및 메타 저장
            BinaryContent newProfile = storeBinaryContentMeta(profileRequest.get());
            user.setProfile(newProfile);
        }

        user.update(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                user.getProfile()
        );

        userStatusRepository.findByUserId(user.getId()).ifPresent(status -> {
            status.update(Instant.now());
        });

        User updated = userRepository.save(user);
        return userMapper.toDto(updated);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 id를 가진 유저는 없습니다.");
        }
        userRepository.deleteById(userId);
    }
}