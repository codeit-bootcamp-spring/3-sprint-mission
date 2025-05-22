package com.sprint.mission.discodeit.service.jcf;

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

public class JCFUserService implements UserService {
    // 싱글톤 인스턴스
    private static volatile JCFUserService instance;
    private final UserRepository userRepository;

    // private 생성자로 외부 인스턴스화 방지
    private JCFUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 팩토리 메서드: 싱글톤 인스턴스 생성 및 반환
    public static JCFUserService getInstance(UserRepository userRepository) {
        JCFUserService result = instance;
        if (result == null) {
            synchronized (JCFUserService.class) {
                result = instance;
                if (result == null) {
                    result = new JCFUserService(userRepository);
                    instance = result;
                }
            }
        }
        return result;
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
        return new UserDto(user.getUserId(), user.getCreatedAt(), user.getUpdatedAt(), user.getUsername(), user.getEmail(), user.getProfileId(), false); // 'online' 필드는 임의로 false 설정
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
                    .map(user -> new UserDto(user.getUserId(), user.getCreatedAt(), user.getUpdatedAt(), user.getUsername(), user.getEmail(), user.getProfileId(), false)) // 'online' 필드는 임의로 false 설정
                    .collect(Collectors.toList());
    }

    @Override
    public User updateUser(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> profileCreateRequest) {
        // Optional 처리
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID입니다."));
        user.update(userUpdateRequest.newUsername(), userUpdateRequest.newEmail(), userUpdateRequest.newPassword(), null);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        // Optional 처리
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID입니다."));
        userRepository.deleteById(userId);
    }
}
