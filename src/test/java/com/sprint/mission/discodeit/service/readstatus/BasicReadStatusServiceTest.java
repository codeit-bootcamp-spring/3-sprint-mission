package com.sprint.mission.discodeit.service.readstatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusException;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.ReadStatusFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;

@ExtendWith(MockitoExtension.class)
public class BasicReadStatusServiceTest {

  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ChannelRepository channelRepository;

  @InjectMocks
  private BasicReadStatusService readStatusService;

  private User user;
  private Channel channel;
  private UUID userId;
  private UUID channelId;
  private ReadStatus readStatus;

  @BeforeEach
  public void setUp() {
    user = UserFixture.createValidUserWithId();
    channel = ChannelFixture.createValidChannelWithId();
    userId = user.getId();
    channelId = channel.getId();
    readStatus = ReadStatusFixture.create(user, channel);
  }

  @Nested
  class Create {

    @Test
    public void 읽기_상태가_정상적으로_생성돼야_한다() {
      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
      given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);

      ReadStatusResponse createdStatus = readStatusService.create(userId, channelId);

      assertNotNull(createdStatus);
      assertEquals(userId, createdStatus.userId());
      assertEquals(channelId, createdStatus.channelId());
    }

    @Test
    public void 존재하지_않는_사용자의_읽기_상태_생성_시_예외가_발생해야_한다() {
      // given: 사용자가 존재하지 않음
      UUID userId = UUID.randomUUID();
      given(userRepository.findById(userId)).willReturn(Optional.empty());

      ReadStatusException exception = assertThrows(ReadStatusException.class, () -> {
        readStatusService.create(userId, channelId);
      });

      assertEquals(ErrorCode.READ_STATUS_INVALID_USER_OR_CHANNEL, exception.getErrorCode());
    }

    @Test
    public void 존재하지_않는_채널의_읽기_상태_생성_시_예외가_발생해야_한다() {
      // given: 채널이 존재하지 않음
      UUID channelId = UUID.randomUUID();
      given(userRepository.findById(userId)).willReturn(Optional.of(UserFixture.createValidUser()));
      given(channelRepository.findById(channelId)).willReturn(Optional.empty());

      ReadStatusException exception = assertThrows(ReadStatusException.class, () -> {
        readStatusService.create(userId, channelId);
      });

      assertEquals(ErrorCode.READ_STATUS_INVALID_USER_OR_CHANNEL, exception.getErrorCode());
    }

    @Test
    public void 읽기_상태_생성_시_중복되는_경우_예외가_발생해야_한다() {
      // given: 중복된 ReadStatus가 존재
      given(userRepository.findById(userId)).willReturn(Optional.of(user));
      given(channelRepository.findById(channelId)).willReturn(
          Optional.of(channel));
      given(readStatusRepository.findByUserIdAndChannelId(userId, channelId))
          .willReturn(Optional.of(readStatus));

      ReadStatusException exception = assertThrows(ReadStatusException.class,
          () -> {
            readStatusService.create(userId, channelId);
          });
      assertEquals(ErrorCode.READ_STATUS_ALREADY_EXISTS, exception.getErrorCode());
    }
  }

  @Nested
  class Read {

    @Test
    public void 존재하지_않는_읽기_상태를_조회_시도하면_예외가_발생해야_한다() {
      // given: ReadStatus 객체가 리포지토리에 존재하지 않음
      UUID id = UUID.randomUUID();
      given(readStatusRepository.findById(id)).willReturn(Optional.empty());

      ReadStatusException exception = assertThrows(ReadStatusException.class,
          () -> {
            readStatusService.find(id);
          });
      assertEquals(ErrorCode.READ_STATUS_NOT_FOUND, exception.getErrorCode());
    }
  }

  @Nested
  class Update {

    @Test
    public void 읽기_상태는_정상적으로_업데이트_돼야_한다() {
      given(readStatusRepository.findById(readStatus.getId())).willReturn(Optional.of(readStatus));
      given(readStatusRepository.save(any(ReadStatus.class))).willReturn(readStatus);

      ReadStatusResponse updatedStatus = readStatusService.update(readStatus.getId());

      assertNotNull(updatedStatus);
    }
  }

  @Nested
  class Delete {

    @Test
    public void 존재하지_않는_읽기_상태_삭제_시도하면_예외가_발생해야_한다() {
      // given: 삭제할 ReadStatus 객체가 없음
      UUID id = UUID.randomUUID();
      given(readStatusRepository.findById(id)).willReturn(Optional.empty());

      ReadStatusException exception = assertThrows(ReadStatusException.class,
          () -> {
            readStatusService.delete(id);
          });
      assertEquals(ErrorCode.READ_STATUS_NOT_FOUND, exception.getErrorCode());
    }
  }
}