package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.ToString;

@ToString
public abstract class ImmutableAuditable implements Serializable {

  protected final UUID id;
  protected final Instant createdAt;

  protected ImmutableAuditable() {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
