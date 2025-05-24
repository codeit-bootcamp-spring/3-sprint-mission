package com.sprint.mission.discodeit.entitiy;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Channel implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID id;
  private Instant createdAt;
  private Instant updatedAt;
  private ChannelType type;
  private String name;
  private String description;

  public Channel(String name, ChannelType type, String description) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
  }

  @Override
  public String toString() {
    return "Channel{" +
        "id=" + id +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", type=" + type +
        ", newName='" + name + '\'' +
        ", newDescription='" + description + '\'' +
        '}';
  }
}
