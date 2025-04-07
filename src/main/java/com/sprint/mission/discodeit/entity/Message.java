package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.util.UUID;

@Getter
public class Message {
  private final UUID id;
  private final Long createdAt;
  private Long updatedAt;

  private String content;
  private UUID channelId;
  private UUID senderId;

  public Message( String content,UUID channelId, UUID senderId) {
    this.id = UUID.randomUUID();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = System.currentTimeMillis();
    this.content = content;
    this.channelId = channelId;
    this.senderId = senderId;
  }

  public void updateContent(String content) {
    this.content = content;
    this.updatedAt = System.currentTimeMillis();
  }

  @Override
  public String toString() {
    return "Message{" +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", content='" + content + '\'' +
        ", channelId=" + channelId +
        ", senderId=" + senderId +
        '}';
  }
}
