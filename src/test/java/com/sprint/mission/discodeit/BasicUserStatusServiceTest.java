package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasicUserStatusServiceTest {

  private UserStatusRepository userStatusRepository;
  private UserRepository userRepository;
  private BasicUserStatusService service;

  @BeforeEach
  void setUp() {
    userStatusRepository = mock(UserStatusRepository.class);
    userRepository = mock(UserRepository.class);
    service = new BasicUserStatusService(userStatusRepository, userRepository);
  }

  @Test
  void find_shouldReturnUserStatus() {
    UUID id = UUID.randomUUID();
    UserStatus status = new UserStatus(id, Instant.now());
    when(userStatusRepository.findById(id)).thenReturn(Optional.of(status));

    assertEquals(status, service.find(id));
  }

  @Test
  void find_shouldThrowIfNotFound() {
    UUID id = UUID.randomUUID();
    when(userStatusRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> service.find(id));
  }

  @Test
  void findAll_shouldReturnAllStatuses() {
    List<UserStatus> statuses = List.of(new UserStatus(UUID.randomUUID(), Instant.now()));
    when(userStatusRepository.findAll()).thenReturn(statuses);

    assertEquals(statuses, service.findAll());
  }

  @Test
  void create_shouldSaveUserStatus() {
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();

    when(userRepository.existsById(userId)).thenReturn(true);
    when(userStatusRepository.findByUserId(userId)).thenReturn(Optional.empty());

    UserStatus expected = new UserStatus(userId, now);
    when(userStatusRepository.save(any())).thenReturn(expected);

    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, now);
    UserStatus result = service.create(request);

    assertEquals(expected, result);
  }

  @Test
  void create_shouldThrowIfUserNotExists() {
    UUID userId = UUID.randomUUID();
    when(userRepository.existsById(userId)).thenReturn(false);

    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());
    assertThrows(NoSuchElementException.class, () -> service.create(request));
  }

  @Test
  void create_shouldThrowIfAlreadyExists() {
    UUID userId = UUID.randomUUID();
    when(userRepository.existsById(userId)).thenReturn(true);
    when(userStatusRepository.findByUserId(userId)).thenReturn(Optional.of(mock(UserStatus.class)));

    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());
    assertThrows(IllegalArgumentException.class, () -> service.create(request));
  }

  @Test
  void update_shouldUpdateLastAccessedAt() {
    UUID statusId = UUID.randomUUID();
    Instant newTime = Instant.now();
    UserStatus status = new UserStatus(UUID.randomUUID(), Instant.now());
    when(userStatusRepository.findById(statusId)).thenReturn(Optional.of(status));
    when(userStatusRepository.save(status)).thenReturn(status);

    UserStatusUpdateRequest request = new UserStatusUpdateRequest(newTime);
    UserStatus result = service.update(statusId, request);

    assertEquals(status, result);
  }

  @Test
  void updateByUserId_shouldUpdateAndReturn() {
    UUID userId = UUID.randomUUID();
    Instant time = Instant.now();
    UserStatus status = new UserStatus(userId, Instant.now());
    when(userStatusRepository.findByUserId(userId)).thenReturn(Optional.of(status));
    when(userStatusRepository.save(status)).thenReturn(status);

    UserStatusUpdateRequest request = new UserStatusUpdateRequest(time);
    UserStatus result = service.updateByUserId(userId, request);

    assertEquals(status, result);
  }

  @Test
  void delete_shouldDeleteAndReturn() {
    UUID id = UUID.randomUUID();
    UserStatus status = new UserStatus(id, Instant.now());
    when(userStatusRepository.findById(id)).thenReturn(Optional.of(status));

    UserStatus result = service.delete(id);

    verify(userStatusRepository).deleteById(id);
    assertEquals(status, result);
  }
}