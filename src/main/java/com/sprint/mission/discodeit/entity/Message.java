package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
  private static final long serialVersionUID = 1L;

  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  private String content;
  private UUID channelId;
  private UUID senderId;
  private List<UUID> attachmentIds;

  public Message(String content, UUID channelId, UUID senderId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    this.content = content;
    this.channelId = channelId;
    this.senderId = senderId;
    this.attachmentIds = new ArrayList<>();
  }

  // 첨부파일 추가
  public void addAttachment(UUID attachmentId) {
    this.attachmentIds.add(attachmentId); // 첨부파일 리스트에 추가
  }

  public void updateContent(String content) {
    this.content = content;
    this.updatedAt = Instant.now();
  }

  @Override
  public String toString() {
    return "Message{" +
        "id=" + id +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", content='" + content + '\'' +
        ", channelId=" + channelId +
        ", senderId=" + senderId +
        '}';
  }
}
