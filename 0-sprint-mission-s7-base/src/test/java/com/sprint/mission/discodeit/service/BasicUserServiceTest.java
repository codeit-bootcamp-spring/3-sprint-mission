package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("UserService 단위 테스트")
public class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserStatusRepository userStatusRepository;

    @InjectMocks
    private BasicUserService userService;


    @Test
    @DisplayName("User 생성 -  case : success")
    void createUserSuccess() {
        UserCreateRequest userCreateRequest = new UserCreateRequest("KHG", "KHG@test.com", "009874");
        User user = new User(userCreateRequest.username(), userCreateRequest.email(), userCreateRequest.password(), null);
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, true);
        UserStatus userStatus = new UserStatus(user, Instant.now());

        given(userRepository.existsByUsername(userCreateRequest.username())).willReturn(false);
        given(userRepository.existsByEmail(userCreateRequest.email())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
        given(userMapper.toDto(any(User.class))).willReturn(userDto);

        UserDto result = userService.create(userCreateRequest, Optional.empty());
        assertThat(result.username()).isEqualTo("KHG");
    }

    @Test
    @DisplayName("User 생성 - case : 중복된 이름으로 인한 failed")
    void createUserFail() {
        UserCreateRequest userCreateRequest = new UserCreateRequest("KHG", "KHG@test.com",
            "009874");
        User user = new User(userCreateRequest.username(), userCreateRequest.email(),
            userCreateRequest.password(), null);

        given(userRepository.existsByUsername(user.getUsername())).willReturn(true);
        assertThatThrownBy(() -> userService.create(userCreateRequest, Optional.empty()))
            .isInstanceOf(UserNameAlreadyExistsException.class)
            .hasMessageContaining("중복된 유저 이름입니다.");
    }

    @Test
    @DisplayName("User 조회 - case : success")
    void findUserSuccess() {
        UUID userId = UUID.randomUUID();
        User user = new User("KHG", "KHG@test.com", "009874", null);
        UserDto expectedDto = new UserDto(userId, "KHG", "KHG@test.com", null, true);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMapper.toDto(user)).willReturn(expectedDto);

        UserDto result = userService.find(userId);

        assertThat(result.username()).isEqualTo("KHG");
        assertThat(result.email()).isEqualTo("KHG@test.com");
    }

    @Test
    @DisplayName("User 조회 - case : 존재하지 않는 유저로 인한 failed")
    void findUserFail() {
        UUID userId = UUID.randomUUID();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.find(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("유저를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("User 수정 - case : success")
    void updatedUserSuccess() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User("test", "test@test.com","9874",null);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("KHG","KHG@test.com","009874");
        UserDto expectedDto = new UserDto(userId, userUpdateRequest.newUsername(), userUpdateRequest.newEmail(), null, true);

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.existsByUsername(userUpdateRequest.newUsername())).willReturn(false);
        given(userMapper.toDto(existingUser)).willReturn(expectedDto);

        UserDto result = userService.update(userId, userUpdateRequest, Optional.empty());

        assertThat(result.username()).isEqualTo(userUpdateRequest.newUsername());
        assertThat(result.email()).isEqualTo(userUpdateRequest.newEmail());
        assertThat(result.online()).isTrue();
    }

    @Test
    @DisplayName("User 수정 - case : 중복된 이메일로 인한 failed")
    void updateUserFail() {
        UUID userID = UUID.randomUUID();
        User existngUser = new User("test", "test@test.com", "9874", null);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("KHG","test@test.com","009874");

        given(userRepository.findById(userID)).willReturn(Optional.of(existngUser));
        given(userRepository.existsByEmail(userUpdateRequest.newEmail())).willReturn(true);

        assertThatThrownBy(() -> userService.update(userID, userUpdateRequest, Optional.empty()))
            .isInstanceOf(UserEmailAlreadyExistsException.class)
            .hasMessageContaining("중복된 이메일입니다.");
    }

    @Test
    @DisplayName("User 삭제 - case : success")
    void deleteUserSuccess() {
        UUID userId = UUID.randomUUID();

        given(userRepository.existsById(userId)).willReturn(true);
        willDoNothing().given(userRepository).deleteById(userId);

        userService.delete(userId);

        then(userRepository).should().deleteById(userId);
    }

    @Test
    @DisplayName("User 삭제 - case : 존재하지 않는 유저로 인한 failed")
    void deleteUserFail() {
        UUID userId = UUID.randomUUID();

        given(userRepository.existsById(userId)).willReturn(false);

        assertThatThrownBy(() -> userService.delete(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("유저를 찾을 수 없습니다.");

        then(userRepository).should(never()).deleteById(userId);
    }
}



