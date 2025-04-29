package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.common.exception.AuthException;
import com.sprint.mission.discodeit.dto.data.UserResponse;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest_mock {

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
    userStatus = UserStatusFixture.createValidUserStatus(user.getId());
    loginRequest = new LoginRequest(user.getName(), user.getPassword());
  }

  @Test
  @DisplayName("유효한 credentials로 로그인 시 UserResponse 반환")
  void loginSuccess() {
    // given
    when(userRepository.findByNameWithPassword(user.getName(), user.getPassword())).thenReturn(
        Optional.ofNullable(user));
    when(userStatusRepository.findByUserId(user.getId()))
        .thenReturn(Optional.of(userStatus));

    // when
    UserResponse response = authService.login(loginRequest);

    System.out.println(response);

    // Then
    assertThat(response).isNotNull();
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(toUserResponse(user, userStatus));

    verify(userRepository).findByNameWithPassword(loginRequest.userName(), loginRequest.password());
  }

  @Test
  @DisplayName("존재하지 않는 사용자 이름으로 로그인 시 예외 발생")
  void loginFail_UserNotFound() {
    // given
    String nonExistingUserName = "non-existing-user";
    String nonExistingPassword = "pwd";
    LoginRequest nonExistingRequest = new LoginRequest(nonExistingUserName, nonExistingPassword);
    given(
        userRepository.findByNameWithPassword(nonExistingUserName, nonExistingPassword)).willReturn(
        Optional.empty());

    // When & Then
    assertThrows(AuthException.class,
        () -> authService.login(nonExistingRequest),
        "예외 메시지 검증");

    verify(userRepository).findByNameWithPassword(nonExistingUserName, nonExistingPassword);
  }

  private UserResponse toUserResponse(User user, UserStatus status) {
    return new UserResponse(
        user.getId(),
        user.getEmail(),
        user.getName(),
        status.isCurrentlyActive(),
        user.getProfileImageId()
    );
  }
}