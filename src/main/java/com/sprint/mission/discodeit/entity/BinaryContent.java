package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
  private static final long serialVersionUID = 1L;
  private final UUID id;
  private final Instant createdAt;

  private String fileName; // 파일 이름 (예: "123.png", "456.pdf")
  private UUID userId; // 프로필 이미지와 관련된 유저 ID
  private UUID messageId; // 메시지와 관련된 파일 (선택적)

  public BinaryContent(String fileName, UUID userId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.fileName = fileName;
    this.userId = userId;
  }

  public BinaryContent(String fileName, UUID userId, UUID messageId) {
    this(fileName, userId);
    this.messageId = messageId;
  }

  @Override
  public String toString() {
    return "BinaryContent{" +
        "fileName='" + fileName + '\'' +
        ", userId=" + userId +
        ", messageId=" + messageId +
        '}';
  }
}

