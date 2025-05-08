package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReadStatus implements Serializable {

  @Serial
  private static final long serialVersionUID = -6861799438879244084L;

  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  private Instant lastReadAt;
  private final UUID userId;
  private final UUID channelId;

  private ReadStatus(UUID userId, UUID channelId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = getCreatedAt();
  }

  public static ReadStatus create(UUID userId, UUID channelId) {
    return new ReadStatus(userId, channelId);
  }

  public static ReadStatus create(ReadStatusCreateRequest request) {
    return new ReadStatus(request.userId(), request.channelId());
  }

  public void touch() {
    this.updatedAt = Instant.now();
  }

  public void updateLastReadAt() {
    this.lastReadAt = Instant.now();
    touch();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ReadStatus readStatus)) {
      return false;
    }

    return Objects.equals(id, readStatus.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}