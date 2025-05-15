package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserCreateRequest userCreateRequest, BinaryContentCreateRequest profileCreateRequest) {
        if (userRepository.existsByEmail(userCreateRequest.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByUsername(userCreateRequest.username())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
        }

        UUID profileId = null;
        if (profileCreateRequest != null) {
            BinaryContent profile = new BinaryContent(
                    profileCreateRequest.fileName(),
                    (long) profileCreateRequest.data().length,
                    profileCreateRequest.contentType(),
                    profileCreateRequest.data()
            );
            profileId = binaryContentRepository.save(profile).getId();
        }

        User user = new User(
                userCreateRequest.username(),
                userCreateRequest.email(),
                userCreateRequest.password(),
                profileId
        );

        User createdUser = userRepository.save(user);
        userStatusRepository.save(new UserStatus(createdUser.getId(), Instant.now()));

        return createdUser;
    }

    @Override
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다."));

        Boolean online = userStatusRepository.findByUserId(userId)
                .map(UserStatus::isOnline)
                .orElse(null);

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online
        );
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    Boolean online = userStatusRepository.findByUserId(user.getId())
                            .map(UserStatus::isOnline)
                            .orElse(null);
                    return new UserDto(
                            user.getId(),
                            user.getCreatedAt(),
                            user.getUpdatedAt(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getProfileId(),
                            online
                    );
                })
                .toList();
    }

    @Override
    public User update(UUID userId, UserUpdateRequest request, BinaryContentCreateRequest profileCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다."));

        UUID profileId = user.getProfileId();

        if (profileCreateRequest != null) {
            if (profileId != null) {
                binaryContentRepository.deleteById(profileId);
            }

            BinaryContent newProfile = new BinaryContent(
                    profileCreateRequest.fileName(),
                    (long) profileCreateRequest.data().length,
                    profileCreateRequest.contentType(),
                    profileCreateRequest.data()
            );
            profileId = binaryContentRepository.save(newProfile).getId();
        }

        user.update(request.username(), request.email(), request.password(), profileId);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 사용자를 찾을 수 없습니다."));

        Optional.ofNullable(user.getProfileId())
                .ifPresent(binaryContentRepository::deleteById);

        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
}