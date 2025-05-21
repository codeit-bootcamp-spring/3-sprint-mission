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

  private UUID userId; // 프로필 이미지와 관련된 유저 ID
  private UUID messageId; // 메시지와 관련된 파일 (선택적)

  private String fileName; // 파일 이름 (예: "123.png", "456.pdf")
  private byte[] data;          // 이미지 바이너리 데이터
  private String contentType;   // MIME 타입 예: "image/jpeg"

  public BinaryContent(String fileName, UUID userId, byte[] data, String contentType) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.fileName = fileName;
    this.userId = userId;
    this.data = data;
    this.contentType = contentType;
  }

  public BinaryContent(String fileName, UUID userId, UUID messageId, byte[] data, String contentType) {
    this(fileName, userId, data, contentType);
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

