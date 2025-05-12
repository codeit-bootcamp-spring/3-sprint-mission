package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Channel implements Serializable {

  @Serial
  private static final long serialVersionUID = 4947061877205205272L;

  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  private final ChannelType type;
  private String name;
  private String description;

  private Channel(ChannelType type) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.name = null;
    this.description = null;
    this.type = type != null ? type : ChannelType.PUBLIC;
  }

  private Channel(String name, String description, ChannelType type) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.name = Objects.requireNonNull(name);
    this.description = description != null ? description : "";
    this.type = type;
  }

  public static Channel createPublic(String name, String description) {
    return new Channel(name, description, ChannelType.PUBLIC);
  }

  public static Channel createPrivate() {
    return new Channel(ChannelType.PRIVATE);
  }

  public void touch() {
    this.updatedAt = Instant.now();
  }

  public void updateName(String name) {
    this.name = name;
    touch();
  }

  public void updateDescription(String description) {
    this.description = description;
    touch();
  }

  public boolean isPublic() {
    return this.type == ChannelType.PUBLIC;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Channel channel)) {
      return false;
    }
    return Objects.equals(id, channel.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
