package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasicReadStatusServiceTest {

  private ReadStatusRepository readStatusRepository;
  private UserRepository userRepository;
  private ChannelRepository channelRepository;
  private BasicReadStatusService service;

  @BeforeEach
  void setUp() {
    readStatusRepository = mock(ReadStatusRepository.class);
    userRepository = mock(UserRepository.class);
    channelRepository = mock(ChannelRepository.class);
    service = new BasicReadStatusService(readStatusRepository, userRepository, channelRepository);
  }

  @Test
  void create_shouldSaveReadStatus() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    Instant time = Instant.now();
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(channelId, userId, time);

    when(userRepository.existsById(userId)).thenReturn(true);
    when(channelRepository.existsById(channelId)).thenReturn(true);
    when(readStatusRepository.findAllByUserId(userId)).thenReturn(List.of());

    ReadStatus expected = new ReadStatus(userId, channelId, time);
    when(readStatusRepository.save(any())).thenReturn(expected);

    ReadStatus result = service.create(request);

    assertEquals(expected, result);
  }

  @Test
  void create_shouldThrowIfUserMissing() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, Instant.now());

    when(userRepository.existsById(userId)).thenReturn(false);

    assertThrows(NoSuchElementException.class, () -> service.create(request));
  }

  @Test
  void create_shouldThrowIfAlreadyExists() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    Instant time = Instant.now();

    ReadStatusCreateRequest request = new ReadStatusCreateRequest(channelId, userId, time);

    when(userRepository.existsById(userId)).thenReturn(true);
    when(channelRepository.existsById(channelId)).thenReturn(true);
    when(readStatusRepository.findAllByUserId(userId)).thenReturn(
        List.of(new ReadStatus(userId, channelId, time)));

    assertThrows(IllegalArgumentException.class, () -> service.create(request));
  }

  @Test
  void find_shouldReturnReadStatus() {
    UUID id = UUID.randomUUID();
    ReadStatus status = mock(ReadStatus.class);
    when(readStatusRepository.findById(id)).thenReturn(Optional.of(status));

    ReadStatus result = service.find(id);
    assertEquals(status, result);
  }

  @Test
  void find_shouldThrowWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(readStatusRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> service.find(id));
  }

  @Test
  void findAllByUserId_shouldReturnList() {
    UUID userId = UUID.randomUUID();
    when(readStatusRepository.findAllByUserId(userId)).thenReturn(List.of());

    List<ReadStatus> result = service.findAllByUserId(userId);
    assertNotNull(result);
  }

  @Test
  void update_shouldUpdateTimestamp() {
    UUID id = UUID.randomUUID();
    Instant newTime = Instant.now();
    ReadStatus status = mock(ReadStatus.class);
    when(readStatusRepository.findById(id)).thenReturn(Optional.of(status));
    when(readStatusRepository.save(status)).thenReturn(status);

    ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(newTime);
    ReadStatus result = service.update(id, request);

    verify(status).update(newTime);
    verify(readStatusRepository).save(status);
    assertEquals(status, result);
  }

  @Test
  void delete_shouldCallRepository() {
    UUID id = UUID.randomUUID();
    service.delete(id);
    verify(readStatusRepository).deleteById(id);
  }
}
