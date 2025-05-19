package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasicUserServiceTest {

  private UserRepository userRepository;
  private UserStatusRepository userStatusRepository;
  private BinaryContentRepository binaryContentRepository;
  private BasicUserService service;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserRepository.class);
    userStatusRepository = mock(UserStatusRepository.class);
    binaryContentRepository = mock(BinaryContentRepository.class);
    service = new BasicUserService(userRepository, userStatusRepository, binaryContentRepository);
  }

  @Test
  void create_shouldCreateUserWithProfileImage() {
    UserCreateRequest request = new UserCreateRequest("john", "john@example.com", "pass");
    BinaryContentCreateRequest image = new BinaryContentCreateRequest("img.jpg", "image/jpeg",
        new byte[5]);

    when(userRepository.existsByUsername("john")).thenReturn(false);
    when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

    UUID binaryId = UUID.randomUUID();
    BinaryContent binaryContent = new BinaryContent("img.jpg", 5L, "image/jpeg", new byte[5]);
    when(binaryContentRepository.save(any())).thenReturn(binaryContent);

    User savedUser = new User("john", "john@example.com", "pass", binaryId);
    when(userRepository.save(any())).thenReturn(savedUser);

    UserResponse response = service.create(request, Optional.of(image));
    assertEquals("john", response.username());
  }

  @Test
  void create_shouldThrowOnDuplicateUsernameOrEmail() {
    UserCreateRequest request = new UserCreateRequest("john", "john@example.com", "pass");

    when(userRepository.existsByUsername("john")).thenReturn(true);
    when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> service.create(request, Optional.empty()));
  }

  @Test
  void find_shouldReturnUserDto() {
    UUID id = UUID.randomUUID();
    User user = new User("john", "john@example.com", "pass", null);
    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    when(userStatusRepository.findByUserId(id)).thenReturn(
        Optional.of(new UserStatus(id, Instant.now())));

    assertNotNull(service.find(id));
  }

  @Test
  void find_shouldThrowIfUserNotFound() {
    UUID id = UUID.randomUUID();
    when(userRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(NoSuchElementException.class, () -> service.find(id));
  }

  @Test
  void update_shouldUpdateUserAndProfile() {

    UUID id = UUID.randomUUID();
    User user = new User("john", "john@example.com", "pass", null);
    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    when(userRepository.existsByUsername("newjohn")).thenReturn(false);
    when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
    when(userRepository.save(any())).thenReturn(user);

    UserUpdateRequest request = new UserUpdateRequest("newjohn", "new@example.com", "newpass");
    BinaryContent mockBinary = new BinaryContent("img.jpg", 3L, "image/jpeg", new byte[3]);
    when(binaryContentRepository.save(any())).thenReturn(mockBinary);
    BinaryContentCreateRequest image = new BinaryContentCreateRequest("img.jpg", "image/jpeg",
        new byte[3]);

    UserResponse result = service.update(id, request, Optional.of(image));

    assertEquals("newjohn", result.username());
  }

  @Test
  void delete_shouldRemoveUserAndProfileAndStatus() {
    UUID id = UUID.randomUUID();
    User user = new User("john", "john@example.com", "pass", UUID.randomUUID());
    when(userRepository.findById(id)).thenReturn(Optional.of(user));

    UserResponse result = service.delete(id);

    verify(binaryContentRepository).deleteById(user.getProfileId());
    verify(userStatusRepository).deleteByUserId(id);
    verify(userRepository).deleteById(id);
    assertEquals("john", result.username());
  }
}
