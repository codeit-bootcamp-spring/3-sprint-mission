package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
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

    private String username;
    private String email;
    private String password;

    @BeforeEach
    void setUp() {
        username = "testUser";
        email = "test@example.com";
        password = "password123";
    }


    @Test
    @DisplayName("사용자 생성 - 성공")
    void create_Success() {
        // Given
        UserCreateRequest request = new UserCreateRequest(username, email, password);
        Optional<BinaryContentCreateRequest> profileRequest = Optional.empty();

        User savedUser = new User(username, email, password, null);
        UserDto expectedDto = new UserDto(UUID.randomUUID(), username, email, null, null);

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByUsername(username)).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

        // When
        UserDto result = userService.create(request, profileRequest);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        then(userRepository).should().existsByEmail(email);
        then(userRepository).should().existsByUsername(username);
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("사용자 생성 - 이메일 중복으로 실패")
    void create_FailWhenEmailAlreadyExists() {
        //given
        UserCreateRequest request = new UserCreateRequest(username, email, password);
        Optional<BinaryContentCreateRequest> profileRequest = Optional.empty();
        given(userRepository.existsByEmail(email)).willReturn(true);

        //when && then
        assertThatThrownBy(() -> userService.create(request, profileRequest))
            .isInstanceOf(UserAlreadyExistException.class);

        then(userRepository).should().existsByEmail(email);
        then(userRepository).should(never()).existsByUsername(username);
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 생성 - 이름 중복으로 실패")
    void create_FailWhenUsernameAlreadyExists() {
        //given
        UserCreateRequest request = new UserCreateRequest(username, email, password);
        Optional<BinaryContentCreateRequest> profileRequest = Optional.empty();

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByUsername(username)).willReturn(true);

        //when && then
        assertThatThrownBy(() -> userService.create(request, profileRequest))
            .isInstanceOf(UserAlreadyExistException.class);

        then(userRepository).should().existsByUsername(username);
        then(userRepository).should().existsByEmail(email);
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 수정 - 성공")
    void update_Success() {
        //given
        UUID userId = UUID.randomUUID();
        String newUsername = "newuser";
        String newEmail = "new@example.com";
        String newPassword = "newpassword123";
        UserUpdateRequest request = new UserUpdateRequest(newUsername, newEmail, newPassword);
        Optional<BinaryContentCreateRequest> profileRequest = Optional.empty();

        User existingUser = new User(username, email, password, null);
        UserDto expectedDto = new UserDto(userId, newUsername, newEmail, null, null);

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.existsByEmail(newEmail)).willReturn(false);
        given(userRepository.existsByUsername(newUsername)).willReturn(false);
        given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

        //when
        UserDto result = userService.update(userId, request, profileRequest);

        //then
        assertThat(result).isEqualTo(expectedDto);
        then(userRepository).should().findById(userId);
        then(userRepository).should().existsByEmail(newEmail);
        then(userRepository).should().existsByUsername(newUsername);
    }

    @Test
    @DisplayName("사용자 수정 - 사용자를 찾을 수 없어 실패")
    void update_FailWhenUserNotFound() {
        // Given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("newuser", "new@example.com",
            "newpassword");
        Optional<BinaryContentCreateRequest> profileRequest = Optional.empty();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.update(userId, request, profileRequest))
            .isInstanceOf(UserNotFoundException.class);

        then(userRepository).should().findById(userId);
        then(userRepository).should(never()).existsByEmail(any());
        then(userRepository).should(never()).existsByUsername(any());
    }

    @Test
    @DisplayName("사용자 삭제 - 성공")
    void delete_Success() {
        //Given
        UUID userId = UUID.randomUUID();
        given(userRepository.existsById(userId)).willReturn(true);

        //When
        userService.delete(userId);

        //Then
        then(userRepository).should().existsById(userId);
        then(userRepository).should().deleteById(userId);
    }

    @Test
    @DisplayName("사용자 삭제 - 사용자를 찾을 수 없어 실패")
    void delete_FailWhenUserNotFound() {
        //Given
        UUID userId = UUID.randomUUID();
        given(userRepository.existsById(userId)).willReturn(false);

        //When
        assertThatThrownBy(() -> userService.delete(userId))
            .isInstanceOf(UserNotFoundException.class);

        //Then
        then(userRepository).should().existsById(userId);
        then(userRepository).should(never()).deleteById(userId);
    }


}
