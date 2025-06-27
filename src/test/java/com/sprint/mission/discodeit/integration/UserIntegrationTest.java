package com.sprint.mission.discodeit.integration;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.sprint.mission.discodeit.controller.UserController;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("사용자 통합 테스트")
@Transactional
public class UserIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("사용자를 생성할 수 있어야한다")
    @Transactional
    void createUser_Success() {
        // Given
        UserCreateRequest request = new UserCreateRequest(
            "testuser",
            "test@example.com",
            "password123"
        );

        // When
        ResponseEntity<UserDto> userDtoResponseEntity = userController.create(request, null);
        UserDto result = userDtoResponseEntity.getBody();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.id()).isNotNull();

        // Database 검증
        assertThat(userRepository.count()).isEqualTo(1);
        User savedUser = userRepository.findByUsername("testuser").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("password123");

    }

    @Test
    @DisplayName("중복된 사용자명으로 사용자생성을 시도하면 예외를 호출해야 한다")
    @Transactional
    void createUser_DuplicateUsername_Fail() {
        // Given - 기존 사용자 생성
        User existingUser = new User("testuser", "existing@example.com", "password", null);
        UserStatus userStatus = new UserStatus(existingUser, Instant.now());
        userRepository.save(existingUser);

        UserCreateRequest request = new UserCreateRequest(
            "testuser", // 중복된 사용자명
            "new@example.com",
            "password123"
        );

        // When & Then
        assertThatThrownBy(() -> userController.create(request, null))
            .isInstanceOf(UserAlreadyExistException.class);

        // Database 검증 - 기존 사용자만 존재해야 함
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userRepository.existsByUsername("testuser")).isTrue();
        assertThat(userRepository.existsByEmail("new@example.com")).isFalse();
    }

    @Test
    @DisplayName("중복된 이메일이 있을시 사용자 생성이 실패해야 한다")
    @Transactional
    void createUser_DuplicateEmail_Fail() {
        // Given - 기존 사용자 생성
        User existingUser = new User("existinguser", "test@example.com", "password", null);
        UserStatus userStatus = new UserStatus(existingUser, Instant.now());
        userRepository.save(existingUser);

        UserCreateRequest request = new UserCreateRequest(
            "newuser",
            "test@example.com", // 중복된 이메일
            "password123"
        );

        // When & Then
        assertThatThrownBy(() -> userController.create(request, null))
            .isInstanceOf(UserAlreadyExistException.class);

        // Database 검증 - 기존 사용자만 존재해야 함
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByUsername("newuser")).isFalse();
    }

    @Test
    @DisplayName("사용자 목록 조회를 할 수 있어야 한다")
    @Transactional
    void findAllUsers_Success() {
        // Given - 테스트 사용자들 생성
        User user1 = new User("user1", "user1@example.com", "password1", null);
        User user2 = new User("user2", "user2@example.com", "password2", null);

        UserStatus status1 = new UserStatus(user1, Instant.now());
        UserStatus status2 = new UserStatus(user2, Instant.now());

        userRepository.save(user1);
        userRepository.save(user2);

        // When
        ResponseEntity<List<UserDto>> all = userController.findAll();
        List<UserDto> result = all.getBody();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserDto::username)
            .containsExactlyInAnyOrder("user1", "user2");
        assertThat(result).extracting(UserDto::email)
            .containsExactlyInAnyOrder("user1@example.com", "user2@example.com");

        // Database 검증
        assertThat(userRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자 목록 조회를 할때 사용자가 없다면 빈 리스트을 출력해야한다")
    @Transactional
    void findAllUsers_EmptyList() {
        // Given - 사용자가 없는 상태

        // When
        ResponseEntity<List<UserDto>> all = userController.findAll();
        List<UserDto> result = all.getBody();

        // Then
        assertThat(result).isEmpty();

        // Database 검증
        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자 삭제를 삭제 할 수 있어야 한다")
    @Transactional
    void deleteUser_Success() {
        // Given - 테스트 사용자 생성
        User user = new User("testuser", "test@example.com", "password", null);
        UserStatus userStatus = new UserStatus(user, Instant.now());
        userRepository.save(user);
        UUID userId = user.getId();

        // 삭제 전 검증
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(userRepository.findById(userId)).isPresent();

        // When
        userController.delete(userId);

        // Then - Database 검증
        assertThat(userRepository.count()).isEqualTo(0);
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 삭제 할 경우 예외를 호출해야한다")
    @Transactional
    void deleteUser_NotFound_Fail() {
        // Given - 존재하지 않는 사용자 ID
        UUID nonExistentUserId = UUID.randomUUID();

        // When & Then
        assertThatThrownBy(() -> userController.delete(nonExistentUserId))
            .isInstanceOf(UserNotFoundException.class);

        // Database 검증
        assertThat(userRepository.count()).isEqualTo(0);
    }

}

