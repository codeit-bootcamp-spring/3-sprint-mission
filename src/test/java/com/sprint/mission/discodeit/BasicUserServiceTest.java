package com.sprint.mission.discodeit.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BasicUserServiceTest {

  private User user;

  @BeforeEach
  void setUp() {
    BinaryContent profile = new BinaryContent("profile.png", 12345L, "image/png",
        new byte[]{1, 2, 3});
    user = new User("oldName", "old@email.com", "oldPassword", profile);
  }

  @Test
  void update_ChangesAllFieldsCorrectly() {
    BinaryContent newProfile = new BinaryContent("new.png", 54321L, "image/png",
        new byte[]{4, 5, 6});
    Instant beforeUpdate = user.getUpdatedAt();

    user.update("newName", "new@email.com", "newPassword", newProfile);

    assertEquals("newName", user.getUsername());
    assertEquals("new@email.com", user.getEmail());
    assertEquals("newPassword", user.getPassword());
    assertEquals(newProfile, user.getProfile());
    assertTrue(user.getUpdatedAt().isAfter(beforeUpdate));
  }

  @Test
  void update_NullFields_DoNotChange() {
    String oldUsername = user.getUsername();
    String oldEmail = user.getEmail();
    String oldPassword = user.getPassword();
    BinaryContent oldProfile = user.getProfile();
    Instant beforeUpdate = user.getUpdatedAt();

    user.update(null, null, null, null);

    assertEquals(oldUsername, user.getUsername());
    assertEquals(oldEmail, user.getEmail());
    assertEquals(oldPassword, user.getPassword());
    assertEquals(oldProfile, user.getProfile());
    assertTrue(user.getUpdatedAt().isAfter(beforeUpdate));
  }
}
