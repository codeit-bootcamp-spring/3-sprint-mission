package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class User implements Serializable {
  private static final long serialVersionUID = 1L;

  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  private String username;
  private String email;
  private UUID profileId;
  private String password;

  public User(String username, String email, String password) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();

    this.username = username;
    this.email = email;
    this.password = password;
  }

  public User(String username, String email) {
   this.id = UUID.randomUUID();
   this.createdAt = Instant.now();
   this.updatedAt = Instant.now();
   this.username = username;
   this.email = email;
   this.password = null;
  }


  public void updateName(String username) {
    this.username = username;
    updatedAt = Instant.now();
  }

  public void updateEmail(String email) {
    this.email = email;
    updatedAt = Instant.now();
  }

  public void setProfileId(UUID profileId) {
    this.profileId = profileId;
    this.updatedAt = Instant.now();
  }

  @Override
  public String toString() {
    return "[" + "username='" + username + ", email='" + email + '\'' + ']';
  }
}
