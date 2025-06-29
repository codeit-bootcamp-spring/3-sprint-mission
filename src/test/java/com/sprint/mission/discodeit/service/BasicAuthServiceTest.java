package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

    @InjectMocks
    private BasicAuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("로그인 성공")
    void shouldLoginSuccessfully() {
        String username = "tester";
        String email = "test@email.com";
        String password = "password1234";

        User user = new User(username, email, password, null);
        UUID generatedId = UUID.randomUUID();

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(userMapper.toResponse(user)).willReturn(
            new UserResponse(generatedId, username, email, null, true)
        );

        LoginRequest request = new LoginRequest(username, password);

        UserResponse response = authService.login(request);

        assertThat(response.id()).isEqualTo(generatedId);
        assertThat(response.username()).isEqualTo(username);
        assertThat(response.email()).isEqualTo(email);
    }


    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void shouldThrowUserNotFoundException_whenUserNotFound() {
        String username = "notfound";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        LoginRequest request = new LoginRequest(username, "password");

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void shouldThrowInvalidPasswordException_whenPasswordIsIncorrect() {
        String username = "tester";
        String email = "tester@email.com";
        String correctPassword = "correct_pw";
        User user = new User(username, email, correctPassword, null);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        LoginRequest request = new LoginRequest(username, "wrong_pw");

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(InvalidPasswordException.class)
            .hasMessage("Invalid password.");
    }
}
