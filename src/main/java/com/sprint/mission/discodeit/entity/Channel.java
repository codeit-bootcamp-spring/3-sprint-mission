package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Channel implements Serializable {

  @Serial
  private static final long serialVersionUID = 7955482681221044662L;
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  private String description;
  private boolean isPrivate;
  private String name;

  public Channel(String name) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
    this.name = name;
  }

  public Channel(String name, String description, boolean isPrivate) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
    this.name = name;
    this.description = description;
    this.isPrivate = isPrivate;
  }

  public void updateName(String name) {
    this.name = name;
    this.updatedAt = Instant.now();
  }

  public void update(String name, String description) {
    if (name != null) {
      this.name = name;
    }
    if (description != null) {
      this.description = description;
    }
    this.updatedAt = Instant.now();
  }
}
