package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.common.exception.UserException;
import com.sprint.mission.discodeit.common.exception.UserStatusException;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
class UserStatusServiceImplTest_mock {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @InjectMocks
  private UserStatusServiceImpl userStatusService;

  private final UUID userId = UUID.randomUUID();

  private User user;

  @BeforeEach
  void init() {
    user = User.create("test@example.com", "TestUser", "password");
  }

  @Nested
  @DisplayName("유저 상태 생성")
  class Create {

    private UserStatusCreateRequest request;

    @BeforeEach
    void setUp() {
      request = new UserStatusCreateRequest(userId);
    }

    @Test
    @DisplayName("유효한 요청이면 정상적으로 생성된다")
    void shouldCreateSuccessfully() {
      // given
      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());
      given(userStatusRepository.save(any(UserStatus.class)))
          .willAnswer(invocation -> invocation.getArgument(0));

      // when
      UserStatus result = userStatusService.create(request);

      // then
      assertThat(result.getUserId()).isEqualTo(userId);
      verify(userRepository).findById(userId);
      verify(userStatusRepository).findByUserId(userId);
      verify(userStatusRepository).save(any(UserStatus.class));
    }

    @Test
    @DisplayName("존재하지 않는 유저이면 예외가 발생한다")
    void shouldThrowExceptionWhenUserNotFound() {
      given(userRepository.findById(userId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> userStatusService.create(request))
          .isInstanceOf(UserException.class);

      verify(userRepository).findById(userId);
      verify(userStatusRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 존재하는 상태이면 예외가 발생한다")
    void shouldThrowExceptionWhenStatusAlreadyExists() {
      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(userStatusRepository.findByUserId(userId)).willReturn(
          Optional.of(UserStatusFixture.createValidUserStatus(userId)));

      assertThatThrownBy(() -> userStatusService.create(request))
          .isInstanceOf(UserStatusException.class);

      verify(userRepository).findById(userId);
      verify(userStatusRepository).findByUserId(userId);
      verify(userStatusRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("유저 상태 조회")
  class Read {

    private UserStatus userStatus;

    @BeforeEach
    void setUp() {
      userStatus = UserStatusFixture.createValidUserStatus(userId);
    }

    @Test
    @DisplayName("ID로 조회할 수 있다")
    void shouldFindById() {
      UUID statusId = UUID.randomUUID();
      given(userStatusRepository.findById(statusId)).willReturn(Optional.of(userStatus));

      UserStatus found = userStatusService.find(statusId);

      assertThat(found).isNotNull();
      verify(userStatusRepository).findById(statusId);
    }

    @Test
    @DisplayName("userId로 조회할 수 있다")
    void shouldFindByUserId() {
      given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));

      Optional<UserStatus> result = userStatusService.findByUserId(userId);

      assertThat(result).isPresent();
      assertThat(result.get().getUserId()).isEqualTo(userId);
      verify(userStatusRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("존재하지 않으면 예외 발생")
    void shouldThrowWhenFindByIdNotFound() {
      UUID statusId = UUID.randomUUID();
      given(userStatusRepository.findById(statusId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> userStatusService.find(statusId))
          .isInstanceOf(UserStatusException.class);

      verify(userStatusRepository).findById(statusId);
    }
  }

  @Nested
  @DisplayName("Update 메서드")
  class Update {

    private UserStatus userStatus;

    @BeforeEach
    void setUp() {
      userStatus = UserStatusFixture.createOfflineUserStatus(userId);
    }

    @Test
    @DisplayName("ID로 상태를 업데이트할 수 있다")
    void shouldUpdateSuccessfully() {
      UUID statusId = userStatus.getId();
      UserStatusUpdateRequest request = new UserStatusUpdateRequest(statusId);
      given(userStatusRepository.findById(statusId)).willReturn(Optional.of(userStatus));

      UserStatus updated = userStatusService.update(request);

      assertThat(updated).isNotNull();
      assertThat(updated.isCurrentlyActive()).isTrue();
      verify(userStatusRepository).findById(statusId);
    }

    @Test
    @DisplayName("userId로 상태를 업데이트할 수 있다")
    void shouldUpdateByUserIdSuccessfully() {
      given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));

      UserStatus updated = userStatusService.updateByUserId(userId);

      assertThat(updated).isNotNull();
      assertThat(updated.isCurrentlyActive()).isTrue();
      verify(userStatusRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("존재하지 않으면 예외 발생")
    void shouldThrowWhenUpdateByUserIdNotFound() {
      given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> userStatusService.updateByUserId(userId))
          .isInstanceOf(UserStatusException.class);

      verify(userStatusRepository).findByUserId(userId);
    }
  }

  @Nested
  @DisplayName("Delete 메서드")
  class Delete {

    private UserStatus userStatus;

    @BeforeEach
    void setUp() {
      userStatus = UserStatusFixture.createValidUserStatus(userId);
    }

    @Test
    @DisplayName("삭제할 수 있다")
    void shouldDeleteSuccessfully() {
      UUID statusId = userStatus.getId();
      given(userStatusRepository.findById(statusId)).willReturn(Optional.of(userStatus));

      userStatusService.delete(statusId);

      verify(userStatusRepository).findById(statusId);
      verify(userStatusRepository).delete(statusId);
    }

    @Test
    @DisplayName("존재하지 않으면 예외 발생")
    void shouldThrowWhenNotFound() {
      UUID statusId = UUID.randomUUID();
      given(userStatusRepository.findById(statusId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> userStatusService.delete(statusId))
          .isInstanceOf(UserStatusException.class);

      verify(userStatusRepository).findById(statusId);
      verify(userStatusRepository, never()).delete(statusId);
    }
  }
}
