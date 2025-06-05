package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.exception.UserStatusException;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserStatusServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private UserStatusMapper userStatusMapper;

  @InjectMocks
  private BasicUserStatusService userStatusService;

  private User user;
  private UserStatus userStatus;
  private UUID userId;

  @BeforeEach
  void init() {
    user = UserFixture.createValidUserWithId();
    userStatus = UserStatusFixture.createWithId(user);
    userId = user.getId();
  }

  @Nested
  class Create {

    @Test
    void 존재하지_않는_유저이면_UserException_예외를_던진다() {
      given(userRepository.findById(userId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> userStatusService.create(userId))
          .isInstanceOf(UserException.class);

      verify(userRepository).findById(userId);
      verify(userStatusRepository, never()).save(any());
    }

    @Test
    void 이미_존재하는_유저_상태이면_UserStatusException_예외를_던진다() {
      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(userStatusRepository.findByUserId(userId))
          .willReturn(Optional.of(UserStatusFixture.createValid(user)));

      assertThatThrownBy(() -> userStatusService.create(userId))
          .isInstanceOf(UserStatusException.class);

      verify(userRepository).findById(userId);
      verify(userStatusRepository).findByUserId(userId);
      verify(userStatusRepository, never()).save(any());
    }
  }

  @Nested
  class Read {

    @Test
    void 존재하지_않는_ID로_조회하면_UserStatusException_예외를_던진다() {
      UUID statusId = UUID.randomUUID();
      given(userStatusRepository.findById(statusId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> userStatusService.find(statusId))
          .isInstanceOf(UserStatusException.class);

      verify(userStatusRepository).findById(statusId);
    }
  }

  @Nested
  class Update {

    @Test
    void 존재하지_않는_userId로_업데이트하면_UserStatusException_예외를_던진다() {
      given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> userStatusService.updateByUserId(userId))
          .isInstanceOf(UserStatusException.class);

      verify(userStatusRepository).findByUserId(userId);
    }
  }

  @Nested
  class Delete {

    @Test
    void ID로_유저_상태를_삭제한다() {
      UUID statusId = userStatus.getId();
      given(userStatusRepository.findById(statusId)).willReturn(Optional.of(userStatus));

      userStatusService.delete(statusId);

      verify(userStatusRepository).findById(statusId);
      verify(userStatusRepository).deleteById(statusId);
    }

    @Test
    void 존재하지_않는_ID로_삭제하면_UserStatusException_예외를_던진다() {
      UUID statusId = UUID.randomUUID();
      given(userStatusRepository.findById(statusId)).willReturn(Optional.empty());

      assertThatThrownBy(() -> userStatusService.delete(statusId))
          .isInstanceOf(UserStatusException.class);

      verify(userStatusRepository).findById(statusId);
      verify(userStatusRepository, never()).deleteById(statusId);
    }
  }

  @Nested
  class WithMapper {

    @BeforeEach
    void setUp() {
      given(userStatusMapper.toResponse(any(UserStatus.class)))
          .willAnswer(invocation -> {
            UserStatus status = invocation.getArgument(0);
            return new UserStatusResponse(
                status.getId(),
                status.getUser().getId(),
                status.getLastActiveAt()
            );
          });
    }

    @Test
    void 유효한_요청이면_유저_상태를_생성한다() {
      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(userStatusRepository.findByUserId(userId)).willReturn(Optional.empty());
      given(userStatusRepository.save(any(UserStatus.class)))
          .willAnswer(invocation -> invocation.getArgument(0));

      UserStatusResponse result = userStatusService.create(userId);

      assertThat(result.userId()).isEqualTo(userId);
      verify(userRepository).findById(userId);
      verify(userStatusRepository).findByUserId(userId);
      verify(userStatusRepository).save(any(UserStatus.class));
    }

    @Test
    void ID로_유저_상태를_업데이트한다() {
      UUID statusId = userStatus.getId();
      given(userStatusRepository.save(userStatus)).willReturn(userStatus);
      given(userStatusRepository.findById(statusId)).willReturn(Optional.of(userStatus));

      UserStatusResponse updated = userStatusService.update(statusId);

      assertThat(updated).isNotNull();
      verify(userStatusRepository).findById(statusId);
    }

    @Test
    void userId로_유저_상태를_업데이트한다() {
      given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));

      UserStatusResponse updated = userStatusService.updateByUserId(userId);

      assertThat(updated).isNotNull();
      verify(userStatusRepository).findByUserId(userId);
    }

    @Test
    void ID로_유저_상태를_조회한다() {
      UUID statusId = UUID.randomUUID();
      given(userStatusRepository.findById(statusId)).willReturn(Optional.of(userStatus));

      UserStatusResponse found = userStatusService.find(statusId);

      assertThat(found).isNotNull();
      verify(userStatusRepository).findById(statusId);
    }

    @Test
    void userId로_유저_상태를_조회한다() {
      given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));

      UserStatusResponse result = userStatusService.findByUserId(userId);

      assertThat(result.userId()).isEqualTo(userId);
      verify(userStatusRepository).findByUserId(userId);
    }
  }
}
