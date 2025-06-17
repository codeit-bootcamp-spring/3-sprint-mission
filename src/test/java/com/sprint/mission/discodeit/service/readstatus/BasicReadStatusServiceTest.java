package com.sprint.mission.discodeit.service.readstatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ReadStatusException;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.ReadStatusFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
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
  private ReadStatusCreateRequest request;
  private ReadStatus readStatus;

  @BeforeEach
  public void setUp() {
    user = UserFixture.createValidUser();
    channel = ChannelFixture.createPublic();
    userId = user.getId();
    channelId = channel.getId();
    readStatus = ReadStatusFixture.create(user, channel);
  }

  @Nested
  class Create {

    @Test
    public void 읽기_상태가_정상적으로_생성돼야_한다() {
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
      when(readStatusRepository.save(any(ReadStatus.class))).thenReturn(readStatus);

      ReadStatusResponse createdStatus = readStatusService.create(userId, channelId);

      assertNotNull(createdStatus);
      assertEquals(userId, createdStatus.userId());
      assertEquals(channelId, createdStatus.channelId());
    }


    @Test
    public void 존재하지_않는_사용자의_읽기_상태_생성_시_예외가_발생해야_한다() {
      // given: 사용자가 존재하지 않음
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      ReadStatusException exception = assertThrows(ReadStatusException.class, () -> {
        readStatusService.create(userId, channelId);
      });

      assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void 존재하지_않는_채널의_읽기_상태_생성_시_예외가_발생해야_한다() {
      // given: 채널이 존재하지 않음
      when(userRepository.findById(userId)).thenReturn(Optional.of(UserFixture.createValidUser()));
      when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

      ReadStatusException exception = assertThrows(ReadStatusException.class, () -> {
        readStatusService.create(userId, channelId);
      });

      assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void 읽기_상태_생성_시_중복되는_경우_예외가_발생해야_한다() {
      // given: 중복된 ReadStatus가 존재
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(channelRepository.findById(channelId)).thenReturn(
          Optional.of(channel));
      when(readStatusRepository.findByUserIdAndChannelId(userId, channelId))
          .thenReturn(Optional.of(readStatus));

      ReadStatusException exception = assertThrows(ReadStatusException.class, () -> {
        readStatusService.create(userId, channelId);
      });

      assertEquals(ErrorCode.ALREADY_EXISTS, exception.getErrorCode());
    }
  }

  @Nested
  class Read {

    @Test
    public void 존재하지_않는_읽기_상태를_조회_시도하면_예외가_발생해야_한다() {
      // given: ReadStatus 객체가 리포지토리에 존재하지 않음
      when(readStatusRepository.findById(userId)).thenReturn(Optional.empty());

      ReadStatusException exception = assertThrows(ReadStatusException.class, () -> {
        readStatusService.find(userId);
      });

      assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }
  }

  @Nested
  class Update {

    @Test
    public void 읽기_상태는_정상적으로_업데이트_돼야_한다() {
      when(readStatusRepository.findById(readStatus.getId())).thenReturn(Optional.of(readStatus));
      when(readStatusRepository.save(any(ReadStatus.class))).thenReturn(readStatus);

      ReadStatusResponse updatedStatus = readStatusService.update(readStatus.getId());

      assertNotNull(updatedStatus);
    }
  }

  @Nested
  class Delete {

    @Test
    public void 존재하지_않는_읽기_상태_삭제_시도하면_예외가_발생해야_한다() {
      // given: 삭제할 ReadStatus 객체가 없음
      when(readStatusRepository.findById(userId)).thenReturn(Optional.empty());

      ReadStatusException exception = assertThrows(ReadStatusException.class, () -> {
        readStatusService.delete(userId);
      });

      assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
    }
  }
}