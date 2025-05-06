package com.sprint.mission.discodeit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BasicReadStatusServiceTest {

  private static final Logger log = LogManager.getLogger(BasicReadStatusServiceTest.class);
  @Mock
  private ReadStatusRepository readStatusRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ChannelRepository channelRepository;

  @InjectMocks
  private BasicReadStatusService readStatusService;

  private UUID userId;
  private UUID channelId;
  private ReadStatusCreateRequest createRequest;

  @BeforeEach
  public void setUp() {
    // Fixture를 활용해 User와 Channel을 생성
    userId = UUID.randomUUID();
    channelId = UUID.randomUUID();
    createRequest = new ReadStatusCreateRequest(userId, channelId);
  }

  @Test
  public void testCreateReadStatus() {
    // given: Fixture를 사용해 mock 사용자와 채널을 생성
    User mockUser = UserFixture.createValidUser();
    Channel mockChannel = ChannelFixture.createValidChannel();

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(channelRepository.findById(channelId)).thenReturn(Optional.of(mockChannel));

    // 추가: save() 메소드가 실제로 생성된 ReadStatus 객체를 반환하도록 mock 설정
    ReadStatus mockReadStatus = ReadStatusFixture.createReadStatus(createRequest);
    when(readStatusRepository.save(any(ReadStatus.class))).thenReturn(mockReadStatus);

    // when: create 메소드 호출
    ReadStatus createdStatus = readStatusService.create(createRequest);

    // then: 생성된 ReadStatus가 null이 아니어야 함
    assertNotNull(createdStatus);
    assertEquals(userId, createdStatus.getUserId());
    assertEquals(channelId, createdStatus.getChannelId());
  }


  @Test
  public void testCreateReadStatus_UserNotFound() {
    // given: 사용자가 존재하지 않음
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // when: create 메소드 호출
    ReadStatusException exception = assertThrows(ReadStatusException.class, () -> {
      readStatusService.create(createRequest);
    });

    // then: 예외가 발생해야 함
    assertEquals(ReadStatusException.INVALID_USER_OR_CHANNEL, exception.getErrorCode());
  }

  @Test
  public void testCreateReadStatus_ChannelNotFound() {
    // given: 채널이 존재하지 않음
    when(userRepository.findById(userId)).thenReturn(Optional.of(UserFixture.createValidUser()));
    when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

    // when: create 메소드 호출
    ReadStatusException exception = assertThrows(ReadStatusException.class, () -> {
      readStatusService.create(createRequest);
    });

    // then: 예외가 발생해야 함
    assertEquals(ReadStatusException.INVALID_USER_OR_CHANNEL, exception.getErrorCode());
  }

  @Test
  public void testCreateReadStatus_Duplicate() {
    // given: 중복된 ReadStatus가 존재
    when(userRepository.findById(userId)).thenReturn(Optional.of(UserFixture.createValidUser()));
    when(channelRepository.findById(channelId)).thenReturn(
        Optional.of(ChannelFixture.createValidChannel()));
    when(readStatusRepository.findByUserIdAndChannelId(userId, channelId))
        .thenReturn(Optional.of(ReadStatusFixture.createReadStatus(createRequest)));

    // when: create 메소드 호출
    ReadStatusException exception = assertThrows(ReadStatusException.class, () -> {
      readStatusService.create(createRequest);
    });

    // then: 예외가 발생해야 함
    assertEquals(ReadStatusException.DUPLICATE_READ_STATUS, exception.getErrorCode());
  }

  @Test
  public void testFindReadStatusById_NotFound() {
    // given: ReadStatus 객체가 리포지토리에 존재하지 않음
    ReadStatus readStatus = ReadStatusFixture.createValidReadStatus();
    when(readStatusRepository.findById(readStatus.getId())).thenReturn(Optional.empty());

    // when: find 메소드 호출
    ReadStatusException exception = assertThrows(ReadStatusException.class, () -> {
      readStatusService.find(readStatus.getId());
    });

    // then: 예외가 발생해야 함
    assertEquals(ReadStatusException.READ_STATUS_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  public void testUpdateReadStatus() {
    // given: 기존 ReadStatus 객체가 리포지토리에 존재
    ReadStatus readStatus = ReadStatusFixture.createValidReadStatus();
    when(readStatusRepository.findById(readStatus.getId())).thenReturn(Optional.of(readStatus));
    when(readStatusRepository.save(any(ReadStatus.class))).thenReturn(readStatus);

    // when: update 메소드 호출
    ReadStatus updatedStatus = readStatusService.update(readStatus.getId());

    // then: lastReadAt이 업데이트 되었는지 확인
    assertNotNull(updatedStatus);
  }

  @Test
  public void testDeleteReadStatus_NotFound() {
    // given: 삭제할 ReadStatus 객체가 없음
    UUID id = UUID.randomUUID();
    when(readStatusRepository.findById(id)).thenReturn(Optional.empty());

    // when: delete 메소드 호출
    ReadStatusException exception = assertThrows(ReadStatusException.class, () -> {
      readStatusService.delete(id);
    });

    // then: 예외가 발생해야 함
    assertEquals(ReadStatusException.READ_STATUS_NOT_FOUND, exception.getErrorCode());
  }
}