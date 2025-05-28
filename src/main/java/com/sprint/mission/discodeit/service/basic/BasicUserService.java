package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserDto;
import com.sprint.mission.discodeit.dto.User.UserFindRequest;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.transaction.Transactional;
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

    @Override
    public User create(UserCreateRequest request, Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
        validateUniqueUser(request);

        BinaryContent profile = binaryContentCreateRequest
                .map(this::saveBinaryContent)
                .orElse(null);

        User user = new User(request.username(), request.email(), request.password(), profile, null);
        User savedUser = userRepository.save(user);

        UserStatus status = new UserStatus(savedUser, Instant.now());
        userStatusRepository.save(status);

        return savedUser;
    }

    private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                (long) request.bytes().length,
                request.contentType(),
                request.bytes()
        );

        return binaryContent;
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

        boolean isOnline = Optional.ofNullable(user.getStatus())
                .map(status -> status.getLastActiveAt().isAfter(Instant.now().minusSeconds(300)))
                .orElse(false);

        return UserDto.builder()
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileId(Optional.ofNullable(user.getProfile()).map(BinaryContent::getId).orElse(null))
                .online(isOnline)
                .build();
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    boolean isOnline = Optional.ofNullable(user.getStatus())
                            .map(status -> status.getLastActiveAt().isAfter(Instant.now().minusSeconds(300)))
                            .orElse(false);

                    return UserDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .profileId(Optional.ofNullable(user.getProfile()).map(BinaryContent::getId).orElse(null))
                            .online(isOnline)
                            .createdAt(user.getCreatedAt())
                            .updatedAt(user.getUpdatedAt())
                            .build();
                }).toList();
    }

    @Override
    public User update(UUID userId, UserUpdateRequest request,
                       Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저는 없습니다."));

        binaryContentCreateRequest.ifPresent(profileRequest -> {
            Optional.ofNullable(user.getProfile())
                    .ifPresent(binaryContentRepository::delete);
            BinaryContent newProfile = saveBinaryContent(profileRequest);
            user.setProfile(newProfile);
        });

        user.update(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                user.getProfile()
        );

        userStatusRepository.findByUserId(user.getId()).ifPresent(status -> {
            status.update(Instant.now());
        });

        return user;
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저는 없습니다."));

        userRepository.delete(user);
    }
}