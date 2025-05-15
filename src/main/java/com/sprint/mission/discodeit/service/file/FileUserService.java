package com.sprint.mission.discodeit.service.file;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

public class FileUserService implements UserService {

    private final UserRepository userRepository;

    public FileUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> binaryContentCreateRequest) {
        if (userCreateRequest.username() == null || userCreateRequest.username().isEmpty()) {
            throw new IllegalArgumentException("사용자 이름은 비어있을 수 없습니다.");
        }
        if (userCreateRequest.email() == null || userCreateRequest.email().isEmpty()) {
            throw new IllegalArgumentException("이메일은 비어있을 수 없습니다.");
        }
        if (userCreateRequest.password() == null || userCreateRequest.password().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 비어있을 수 없습니다.");
        }
        User user = new User(userCreateRequest.username(), userCreateRequest.email(), userCreateRequest.password(), null);
        return userRepository.save(user);
    }

    @Override
    public UserDto getUserById(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID입니다."));
        return new UserDto(user.getUserId(), user.getCreatedAt(), user.getUpdatedAt(), user.getUsername(), user.getEmail(), user.getProfileId(), false);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 이메일입니다."));
        return new UserDto(user.getUserId(), user.getCreatedAt(), user.getUpdatedAt(), user.getUsername(), user.getEmail(), user.getProfileId(), false);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                    .map(user -> new UserDto(user.getUserId(), user.getCreatedAt(), user.getUpdatedAt(), user.getUsername(), user.getEmail(), user.getProfileId(), false))
                    .collect(Collectors.toList());
    }

    @Override
    public User updateUser(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest) {
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID입니다."));

        if (userUpdateRequest.newUsername() != null && !userUpdateRequest.newUsername().isEmpty()) {
            user.updateUsername(userUpdateRequest.newUsername());
        }
        if (userUpdateRequest.newEmail() != null && !userUpdateRequest.newEmail().isEmpty()) {
            user.updateEmail(userUpdateRequest.newEmail());
        }
        if (userUpdateRequest.newPassword() != null && !userUpdateRequest.newPassword().isEmpty()) {
            user.updatePassword(userUpdateRequest.newPassword());
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }
}
