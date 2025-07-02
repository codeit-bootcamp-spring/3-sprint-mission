package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    private BasicUserService userService;

    @BeforeEach
    @DisplayName("테스트 환경 설정 확인")
    void setUp() {
        assertNotNull(userRepository);
        assertNotNull(userStatusRepository);
        assertNotNull(userMapper);
        assertNotNull(binaryContentRepository);
        assertNotNull(binaryContentStorage);
        assertNotNull(userService);
    }

    @Nested
    @DisplayName("사용자 생성 테스트")
    class UserCreateTests {

        @Test
        @DisplayName("사용자 생성 성공 - 프로필 이미지 없음")
        void createUser_Success_WithoutProfile() {
            // Given
            String username = "testuser";
            String email = "test@example.com";
            String password = "!password123";

            UserCreateRequest request = new UserCreateRequest(username, email, password);
            User savedUser = new User(username, email, password, null);
            UserDto expectedDto = new UserDto(UUID.randomUUID(), username, email, null, null);

            given(userRepository.existsByEmail(email)).willReturn(false);
            given(userRepository.existsByUsername(username)).willReturn(false);
            given(userRepository.save(any(User.class))).willReturn(savedUser);
            given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

            // When
            UserDto result = userService.create(request, Optional.empty());

            // Then
            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo(username);
            assertThat(result.email()).isEqualTo(email);

            then(userRepository).should().existsByEmail(email);
            then(userRepository).should().existsByUsername(username);
            then(userRepository).should().save(any(User.class));
            then(userMapper).should().toDto(any(User.class));
            then(binaryContentRepository).shouldHaveNoInteractions();
            then(binaryContentStorage).shouldHaveNoInteractions();

        }

        @Test
        @DisplayName("사용자 생성 실패 - 이메일 중복")
        void createUser_Fail_EmailAlreadyExists() {
            // Given
            String username = "testuser";
            String email = "test@example.com";
            String password = "password123!";

            UserCreateRequest request = new UserCreateRequest(username, email, password);

            given(userRepository.existsByEmail(email)).willReturn(true);

            // When & Then
            assertThatThrownBy(
                () -> userService.create(request, Optional.empty()))
                .isInstanceOf(EmailAlreadyExistsException.class);

            then(userRepository).should().existsByEmail(email);
            then(userRepository).shouldHaveNoMoreInteractions();
            then(binaryContentRepository).shouldHaveNoInteractions();
            then(binaryContentStorage).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("사용자 수정 테스트")
    class UserUpdateTests {

        @Test
        @DisplayName("사용자 수정 성공 - 프로필 이미지 없음")
        void updateUser_Success_WithoutProfile() {
            // Given
            UUID userId = UUID.randomUUID();
            String newUsername = "updateduser";
            String newEmail = "update@example.com";
            String newPassword = "password123!";

            UserUpdateRequest request = new UserUpdateRequest(newUsername, newEmail, newPassword);
            User existingUser = new User("oldUser", "old@user.com", "!password123", null);
            UserDto expectedDto = new UserDto(userId, newUsername, newEmail, null, null);

            given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
            given(userRepository.existsByEmail(newEmail)).willReturn(false);
            given(userRepository.existsByUsername(newUsername)).willReturn(false);
            given(userMapper.toDto(existingUser)).willReturn(expectedDto);

            // When
            UserDto result = userService.update(userId, request, Optional.empty());

            // Then
            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo(newUsername);
            assertThat(result.email()).isEqualTo(newEmail);
            assertThat(result.profile()).isNull();

            then(userRepository).should().findById(userId);
            then(userRepository).should().existsByEmail(newEmail);
            then(userRepository).should().existsByUsername(newUsername);
            then(userMapper).should().toDto(existingUser);
            then(binaryContentRepository).shouldHaveNoInteractions();
            then(binaryContentStorage).shouldHaveNoInteractions();

        }

        @Test
        @DisplayName("사용자 수정 실패 - 존재하지 않는 사용자")
        void updateUser_Fail_NotFoundUser() {
            // Given
            UUID userId = UUID.randomUUID();
            String newUsername = "updateduser";
            String newEmail = "update@example.com";
            String newPassword = "password123!";

            UserUpdateRequest request = new UserUpdateRequest(newUsername, newEmail, newPassword);

            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(
                () -> userService.update(userId, request, Optional.empty())
            ).isInstanceOf(UserNotFoundException.class);

            then(userRepository).should().findById(userId);
            then(userRepository).shouldHaveNoMoreInteractions();
            then(binaryContentRepository).shouldHaveNoInteractions();
            then(binaryContentStorage).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("사용자 삭제 테스트")
    class UserDeleteTests {

        @Test
        @DisplayName("사용자 삭제 성공")
        void deleteUser_Success() {
            // Given
            UUID userId = UUID.randomUUID();

            given(userRepository.existsById(userId)).willReturn(true);

            // When
            userService.delete(userId);

            // Then
            then(userRepository).should().existsById(userId);
            then(userRepository).should().deleteById(userId);
        }

        @Test
        @DisplayName("사용자 삭제 실패 - 존재하지 않는 사용자")
        void deleteUser_Fail_UserNotFound() {
            // Given
            UUID userId = UUID.randomUUID();

            given(userRepository.existsById(userId)).willReturn(false);

            // When & Then
            assertThatThrownBy(
                () -> userService.delete(userId)
            ).isInstanceOf(UserNotFoundException.class);

            then(userRepository).should().existsById(userId);
            then(userRepository).shouldHaveNoMoreInteractions();
        }
    }

}
