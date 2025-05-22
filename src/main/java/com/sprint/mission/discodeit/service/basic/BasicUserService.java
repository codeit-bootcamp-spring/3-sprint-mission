package com.sprint.mission.discodeit.service.basic;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.Optional;
import java.time.Instant;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;



    @Override
    public User createUser(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();
        String password = userCreateRequest.password();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        if (username == null || email == null || password == null) {
            throw new IllegalArgumentException("All fields are required");
        }
        
        UUID nullableProfileId = processProfileImage(optionalProfileCreateRequest, null);

        User user = new User(username, email, password, nullableProfileId);
        User createdUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(createdUser.getUserId(), Instant.now());
        userStatusRepository.save(userStatus);

        return createdUser;
    }

    @Override
    public UserDto getUserById(UUID userId) {
        return userRepository.findById(userId).map(this::toDto).orElseThrow(() -> new NoSuchElementException("User(" + userId + ") not found"));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public User updateUser(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User(" + userId + ") not found"));
        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();
        
        UUID nullableProfileId = processProfileImage(optionalProfileCreateRequest, user);

        user.update(newUsername, newEmail, newPassword, nullableProfileId);

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User(" + userId + ") not found"));

        Optional.ofNullable(user.getProfileId())
                        .ifPresent(binaryContentRepository::deleteById);
        userStatusRepository.deleteByUserId(userId);

        userRepository.deleteById(userId);    
    }

    private UUID processProfileImage(Optional<BinaryContentCreateRequest> optionalProfileCreateRequest, User user) {
        return optionalProfileCreateRequest
            .map(profileRequest -> {
                if (user != null) {
                    Optional.ofNullable(user.getProfileId())
                                    .ifPresent(binaryContentRepository::deleteById);
                }
                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                return binaryContentRepository.save(binaryContent).getId();
            })
            .orElseGet(() -> user != null ? user.getProfileId() : null);
    }

    private UserDto toDto(User user) {
        Boolean online = userStatusRepository.findByUserId(user.getUserId())
                .map(UserStatus::isOnline)
                .orElse(null);

        return new UserDto(
                user.getUserId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online
        );
    }
}
