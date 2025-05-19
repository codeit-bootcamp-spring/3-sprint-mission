package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasicAuthServiceTest {

  private UserRepository userRepository;
  private BasicAuthService authService;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    authService = new BasicAuthService(userRepository);
  }

  @Test
  void login_success() {
    // given
    LoginRequest request = new LoginRequest("john", "password123");
    User user = new User("john", "john@example.com", "password123", null);

    when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

    // when
    UserResponse response = authService.login(request);

    // then
    assertEquals("john", response.username());
    assertEquals("john@example.com", response.email());
  }

  @Test
  void login_userNotFound_shouldThrow() {
    // given
    LoginRequest request = new LoginRequest("nonexistent", "pass");

    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    // then
    assertThrows(IllegalArgumentException.class, () -> authService.login(request));
  }

  @Test
  void login_wrongPassword_shouldThrow() {
    // given
    LoginRequest request = new LoginRequest("john", "wrongpass");
    User user = new User("john", "john@example.com", "correctpass", null);

    when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

    // then
    assertThrows(IllegalArgumentException.class, () -> authService.login(request));
  }
}
