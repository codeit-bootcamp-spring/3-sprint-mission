package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.AuthException;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @InjectMocks
  private BasicAuthService authService;

  private static User user;
  private static UserStatus userStatus;
  private static LoginRequest loginRequest;

  @BeforeEach
  void setUp() {
    user = UserFixture.createValidUser();
    userStatus = mock(UserStatus.class);
    loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
  }

  @Test
  void 유효한_credentials로_로그인_시_UserResponse_반환() {
    // given
    when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
    when(userStatusRepository.findByUserId(user.getId())).thenReturn(Optional.of(userStatus));

    // when
    UserResponse response = authService.login(loginRequest.username(), loginRequest.password());

    // Then
    assertThat(response).isNotNull();
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(toUserResponse(user));

    verify(userRepository).findByUsername(loginRequest.username());
    verify(userStatus).updateLastActiveAt();
  }

  @Test
  void 존재하지_않는_사용자_이름으로_로그인_시_예외_발생() {
    // given
    String nonExistingUserName = "non-existing-user";
    String nonExistingPassword = "pwd";
    LoginRequest nonExistingRequest = new LoginRequest(nonExistingUserName, nonExistingPassword);
    given(
        userRepository.findByUsername(nonExistingUserName)).willReturn(
        Optional.empty());

    // When & Then
    assertThrows(AuthException.class,
        () -> authService.login(nonExistingRequest.username(), nonExistingRequest.password()),
        "예외 메시지 검증");

    verify(userRepository).findByUsername(nonExistingUserName);
  }

  private UserResponse toUserResponse(User user) {
    Boolean isOnline = userStatusRepository.findByUserId(user.getId())
        .map(UserStatus::isOnline).orElse(null);
    return UserResponse.from(user, isOnline);
  }
}