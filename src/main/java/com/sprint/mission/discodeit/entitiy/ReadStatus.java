package com.sprint.mission.discodeit.entitiy;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ReadStatus implements Serializable {

  private UUID id;
  private Instant createdAt;
  private Instant updatedAt;
  private UUID userId;
  private UUID channelId;
  private Instant lastReadAt;

  public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
    this.userId = userId;
    this.channelId = channelId;
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.lastReadAt = lastReadAt;
  }


  @Override
  public String toString() {
    return "ReadStatus{" +
        "id=" + id +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", userId=" + userId +
        ", channelId=" + channelId +
        ", lastReadAt=" + lastReadAt +
        '}';
  }
}
