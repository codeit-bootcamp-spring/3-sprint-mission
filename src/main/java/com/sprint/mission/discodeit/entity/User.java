package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class User implements Serializable {

  @Serial
  private static final long serialVersionUID = 4821055474138999066L;
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  private String username;
  private String email;
  private String password;
  private UUID profileId;

  public User(String username, String email, String password, UUID profileId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();

    this.username = username;
    this.email = email;
    this.password = password;
    this.profileId = profileId;
  }


  public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
    boolean anyValueUpdated = false;
    if (newUsername != null && !newUsername.equals(this.username)) {
      this.username = newUsername;
      anyValueUpdated = true;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
      anyValueUpdated = true;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
      anyValueUpdated = true;
    }
    if (newProfileId != null && !newProfileId.equals(this.profileId)) {
      this.profileId = newProfileId;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }
}