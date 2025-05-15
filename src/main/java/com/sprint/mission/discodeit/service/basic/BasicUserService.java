package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserCreateRequest;
import com.sprint.mission.discodeit.dto.User.UserFindRequest;
import com.sprint.mission.discodeit.dto.User.UserResponse;
import com.sprint.mission.discodeit.dto.User.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User create(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        validateUniqueUser(request);

        UUID nullableProfileId = optionalProfileCreateRequest
                .map(profileRequest -> {
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);

        User user = new User(request.username(), request.email(), request.password(), null);
        if (nullableProfileId != null) {
            user.setProfileId(nullableProfileId);
        }
        User savedUser = userRepository.save(user);

        UserStatus status = new UserStatus(
                Instant.now(),
                savedUser.getId()
        );
        userStatusRepository.save(status);

        return savedUser;
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
    public UserResponse find(UserFindRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저는 없습니다."));

        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(status -> status.getUpdatedAt().isAfter(Instant.now().minusSeconds(300)))
                .orElse(false);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileId(user.getProfileId())
                .online(isOnline)
                .build();
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    boolean isOnline = userStatusRepository.findByUserId(user.getId())
                            .map(status -> status.getUpdatedAt().isAfter(Instant.now().minusSeconds(300)))
                            .orElse(false);

                    return UserResponse.builder()
                            .id(user.getId())
                            .createdAt(user.getCreatedAt())
                            .updatedAt(user.getUpdatedAt())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .profileId(user.getProfileId())
                            .online(isOnline)
                            .build();
                })
                .toList();
    }


    @Override
    public User update(UserUpdateRequest request, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저는 없습니다."));

        UUID nullableProfileId = optionalProfileCreateRequest
                .map(profileRequest -> {
                    Optional.ofNullable(user.getProfileId())
                            .ifPresent(binaryContentRepository::deleteById);

                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);

        user.update(request.newUsername(), request.newEmail(), request.newPassword(), nullableProfileId);

        userRepository.save(user);

        userStatusRepository.findByUserId(user.getId()).ifPresent(status -> {
            status.update(Instant.now());
            userStatusRepository.save(status);
        });

        return userRepository.save(user);
    }


    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("해당 id를 가진 유저는 없습니다.");
        }
        userRepository.deleteById(userId);

        if (userStatusRepository.existsById(userId)) {
            userStatusRepository.deleteById(userId);
        }
    }
}