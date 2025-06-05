package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Message implements Serializable {

  @Serial
  private static final long serialVersionUID = 5091331492371241399L;

  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  private String content;
  private final UUID authorId;
  private final UUID channelId;
  private Instant deletedAt;
  private Set<UUID> attachmentIds;

  private Message(
      String content,
      UUID authorId,
      UUID channelId,
      Instant deletedAt,
      Set<UUID> attachmentIds
  ) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.content = content;
    this.authorId = authorId;
    this.channelId = channelId;
    this.deletedAt = deletedAt;
    this.attachmentIds = attachmentIds != null ? attachmentIds : new HashSet<>();
  }

  public static Message create(String content, UUID userId, UUID channelId,
      Set<UUID> attachmentIds) {
    return new Message(content, userId, channelId, null, attachmentIds);
  }

  public void touch() {
    this.updatedAt = Instant.now();
  }

  public void updateContent(String content) {
    if (deletedAt == null) {
      this.content = content;
      touch();
    }
  }

  public void isDeleted() {
    this.deletedAt = Instant.now();
  }

  public void delete() {
    if (deletedAt == null) {
      deletedAt = Instant.now();
      touch();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Message message)) {
      return false;
    }

    return Objects.equals(id, message.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}