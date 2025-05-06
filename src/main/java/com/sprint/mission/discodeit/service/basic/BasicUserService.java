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
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest) {
        // 중복 체크
        List<User> users = userRepository.findAll();
        if (users.stream().anyMatch(u -> u.getUsername().equals(userCreateRequest.username()))) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (users.stream().anyMatch(u -> u.getEmail().equals(userCreateRequest.email()))) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 사용자 생성
        User user = new User(userCreateRequest.username(), userCreateRequest.email(), userCreateRequest.password());

        // 프로필 이미지 처리
        profileCreateRequest.ifPresent(request -> {
            BinaryContent profile = new BinaryContent(request.data());
            binaryContentRepository.save(profile);
            user.setProfileId(profile.getId());
        });

        userRepository.save(user);

        // UserStatus 생성
        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

        return user;
    }

    @Override
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElse(new UserStatus(userId));

        return new UserDto(user, status.isConnected());
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus status = userStatusRepository.findByUserId(user.getId())
                            .orElse(new UserStatus(user.getId()));
                    return new UserDto(user, status.isConnected());
                })
                .toList();
    }

    @Override
    public User update(UUID userId, UserUpdateRequest request, Optional<BinaryContentCreateRequest> profileCreateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.update(request.newUsername(), request.newEmail(), request.newPassword());

        profileCreateRequest.ifPresent(profileReq -> {
            BinaryContent newProfile = new BinaryContent(profileReq.data());
            binaryContentRepository.save(newProfile);
            user.setProfileId(newProfile.getId());
        });

        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // 관련된 도메인 삭제
        Optional.ofNullable(user.getProfileId())
                .ifPresent(binaryContentRepository::deleteById);
        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
}
