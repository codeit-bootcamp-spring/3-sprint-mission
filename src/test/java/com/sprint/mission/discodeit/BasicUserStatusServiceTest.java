package com.sprint.mission.discodeit;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasicUserStatusServiceTest {

  private UserStatusRepository userStatusRepository;
  private UserRepository userRepository;
  private UserStatusMapper userStatusMapper;
  private BasicUserStatusService userStatusService;

  @BeforeEach
  void setUp() {
    userStatusRepository = mock(UserStatusRepository.class);
    userRepository = mock(UserRepository.class);
    userStatusMapper = mock(UserStatusMapper.class);
    userStatusService = new BasicUserStatusService(userStatusRepository, userRepository,
        userStatusMapper);
  }

  @Test
  void create_ShouldReturnUserStatusResponse_WhenValidRequest() {
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();
    User user = new User("username", "email@example.com", "password", null);
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, now);
    UserStatus userStatus = new UserStatus(user, now);
    UserStatusResponse expectedResponse = new UserStatusResponse(userStatus.getId(), userId, now,
        true);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userStatusRepository.findByUser_Id(userId)).thenReturn(Optional.empty());
    when(userStatusRepository.save(any(UserStatus.class))).thenReturn(userStatus);
    when(userStatusMapper.toResponse(userStatus)).thenReturn(expectedResponse);

    UserStatusResponse actualResponse = userStatusService.create(request);

    assertEquals(expectedResponse, actualResponse);
    verify(userRepository).findById(userId);
    verify(userStatusRepository).findByUser_Id(userId);
    verify(userStatusRepository).save(any(UserStatus.class));
    verify(userStatusMapper).toResponse(userStatus);
  }

  @Test
  void update_ShouldReturnUpdatedResponse_WhenValidUpdate() {
    UUID statusId = UUID.randomUUID();
    Instant updatedAt = Instant.now();
    User user = new User("username", "email@example.com", "password", null);
    UserStatus existingStatus = new UserStatus(user, Instant.now().minusSeconds(600));
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(updatedAt);
    UserStatus updatedStatus = new UserStatus(user, updatedAt);
    UserStatusResponse expectedResponse = new UserStatusResponse(statusId, user.getId(), updatedAt,
        true);

    when(userStatusRepository.findById(statusId)).thenReturn(Optional.of(existingStatus));
    when(userStatusRepository.save(existingStatus)).thenReturn(updatedStatus);
    when(userStatusMapper.toResponse(updatedStatus)).thenReturn(expectedResponse);

    UserStatusResponse actual = userStatusService.update(statusId, request);

    assertEquals(expectedResponse.lastAccessedAt(), actual.lastAccessedAt());
  }
}
