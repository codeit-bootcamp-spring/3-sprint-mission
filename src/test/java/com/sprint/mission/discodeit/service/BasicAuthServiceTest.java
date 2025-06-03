package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.AuthException;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
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
  private UserMapper userMapper;

  @InjectMocks
  private BasicAuthService authService;

  private static User user;
  private static UserStatus userStatus;
  private static LoginRequest loginRequest;

  @BeforeEach
  void setUp() {
    BinaryContent profile = BinaryContentFixture.createValid();
    user = UserFixture.createCustomUserWithId(
        "test@test.com",
        "길동쓰",
        "pwd123",
        profile
    );
    userStatus = mock(UserStatus.class);
    user.updateUserStatus(userStatus);

    loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
  }

  @Test
  void 유효한_credentials로_로그인_시_UserResponse_반환() {
    // given
    given(userMapper.toResponse(any(User.class)))
        .willAnswer(invocation -> {
          User user = invocation.getArgument(0);
          BinaryContentResponse profile = new BinaryContentResponse(
              user.getProfile().getId(),
              user.getProfile().getFileName(),
              user.getProfile().getContentType(),
              user.getProfile().getSize()
          );

          return new UserResponse(
              user.getId(),
              user.getUsername(),
              user.getEmail(),
              profile,
              user.getUserStatus().isOnline()
          );
        });
    when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

    // when
    UserResponse response = authService.login(loginRequest.username(), loginRequest.password());

    // Then
    assertThat(response).isNotNull();
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(userMapper.toResponse(user));

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
}