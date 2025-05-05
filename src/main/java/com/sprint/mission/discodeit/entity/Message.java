package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Message implements Serializable {

  @Serial
  private static final long serialVersionUID = 3163684452300404179L;
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  private final UUID channelId;
  private final UUID authorId;
  private String content;

  private final List<UUID> attachmentIds;

  public Message(UUID authorId, UUID channelId, String content) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
    this.authorId = authorId;
    this.channelId = channelId;
    this.content = content;
    this.attachmentIds = new ArrayList<>();
  }

  public void update(String content) {
    this.content = content;
    this.updatedAt = Instant.now();
  }


  public void addAttachment(UUID binaryContentId) {
    this.attachmentIds.add(binaryContentId);
    this.updatedAt = Instant.now();
  }

  public void removeAttachment(UUID binaryContentId) {
    this.attachmentIds.remove(binaryContentId);
    this.updatedAt = Instant.now();
  }
}
