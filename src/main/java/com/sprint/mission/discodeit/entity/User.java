package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;
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

  public User(String username) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
    this.username = username;
  }

  public User(String username, String email, String password) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public void updateUsername(String name) {
    this.username = name;
    this.updatedAt = Instant.now();
  }

  public void updateProfile(UUID profileId) {
    this.profileId = profileId;
    this.updatedAt = Instant.now();
  }

  public void update(String username, String email) {
    this.username = username;
    this.email = email;
    this.updatedAt = Instant.now();
  }

  public void update(Optional<String> username, Optional<String> email) {
    username.ifPresent(value -> this.username = value);
    email.ifPresent(value -> this.email = value);
    this.updatedAt = Instant.now();
  }
}