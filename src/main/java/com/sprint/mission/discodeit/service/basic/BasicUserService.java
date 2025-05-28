package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User createUser(UserCreateRequest userRequest,
        Optional<BinaryContentCreateRequest> profileRequest) {
        String username = userRequest.username();
        String email = userRequest.email();

        if (isEmailDuplicate(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
        }

        if (isNameDuplicate(username)) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다: " + username);
        }

        User user = new User(username, email, userRequest.password());
        User createdUser = userRepository.save(user);

        // UserStatus 생성
        Instant now = Instant.now();
        UserStatus status = new UserStatus(createdUser.getId(), now);
        userStatusRepository.save(status);

        // Optional 프로필 이미지 처리
        profileRequest
            .filter(BinaryContentCreateRequest::isValid)
            .ifPresent(profile -> {
                BinaryContent binaryContent = new BinaryContent(profile.fileName(), user.getId(),
                    profile.bytes(), profile.contentType());
                binaryContentRepository.save(binaryContent);
                user.setProfileId(binaryContent.getId());
                userRepository.save(user);
            });

        return createdUser;
    }

    @Override
    public Optional<UserDto> find(UUID id) {
        return userRepository.findById(id)
            .map(user -> {
                boolean isOnline = userStatusRepository.find(user.getId())
                    .map(UserStatus::isOnline)
                    .orElse(false);
                return new UserDto(user, isOnline);
            });
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
            .map(user -> {
                boolean isOnline = userStatusRepository.findByUserId(user.getId())
                    .map(UserStatus::isOnline)
                    .orElse(false);
                return new UserDto(user, isOnline);
            })
            .collect(Collectors.toList());
    }

    @Override
    public User update(UUID userId, UserUpdateRequest userUpdateRequest,
        Optional<BinaryContentCreateRequest> profileCreateRequest) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + userId));

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();

        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다: " + newEmail);
        }
        if (userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException(
                "이미 존재하는 이메일입니다: " + newUsername + " already exists");
        }

        UUID nullableProfileId = profileCreateRequest
            .filter(BinaryContentCreateRequest::isValid)
            .map(profile -> {
                BinaryContent binaryContent = new BinaryContent(
                    profile.fileName(),
                    user.getId(),
                    profile.bytes(),
                    profile.contentType()
                );
                binaryContentRepository.save(binaryContent);
                return binaryContent.getId();
            })
            .orElse(null);

        user.update(newUsername, newEmail, newPassword, nullableProfileId);
        userRepository.update(user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저를 찾을 수 없습니다: " + id));

        binaryContentRepository.delete(id);
        userStatusRepository.delete(id);

        userRepository.delete(id);
    }

    private boolean isEmailDuplicate(String email) {
        return userRepository.findAll().stream()
            .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    private boolean isNameDuplicate(String username) {
        return userRepository.findAll().stream()
            .anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
    }
}