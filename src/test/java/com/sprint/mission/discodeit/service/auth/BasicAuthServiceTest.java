package com.sprint.mission.discodeit.service.auth;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;

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
        profile);
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
              user.getProfile().getSize());

          return new UserResponse(
              user.getId(),
              user.getUsername(),
              user.getEmail(),
              profile,
              user.getUserStatus().isOnline());
        });
    given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

    // when
    UserResponse response = authService.login(loginRequest.username(), loginRequest.password());

    // Then
    assertThat(response).isNotNull();
    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(userMapper.toResponse(user));

    then(userRepository).should().findByUsername(loginRequest.username());
    then(userStatus).should().updateLastActiveAt();
  }

  @Test
  void 존재하지_않는_사용자_이름으로_로그인_시_예외_발생() {
    // given
    String nonExistingUserName = "non-existing-user";
    String nonExistingPassword = "pwd";
    LoginRequest nonExistingRequest = new LoginRequest(nonExistingUserName, nonExistingPassword);
    given(userRepository.findByUsername(nonExistingUserName)).willReturn(Optional.empty());

    // When & Then
    assertThrows(UserException.class,
        () -> authService.login(nonExistingRequest.username(), nonExistingRequest.password()),
        "예외 메시지 검증");

    then(userRepository).should().findByUsername(nonExistingUserName);
  }
}