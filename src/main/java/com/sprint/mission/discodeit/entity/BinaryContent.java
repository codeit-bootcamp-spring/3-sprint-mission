package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
public class BinaryContent implements Serializable {

  @Serial
  private static final long serialVersionUID = 8121899659000317030L;

  public enum ContentType {
    PROFILE_IMAGE,
    MESSAGE_ATTACHMENT
  }

  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  private final byte[] bytes;
  private final String fileName;
  private final String mimeType;
  private final ContentType contentType;
  private final UUID userId;
  private UUID messageId;

  private BinaryContent(UUID id, Instant createdAt, Instant updatedAt, byte[] bytes,
      String fileName, String mimeType, ContentType contentType,
      UUID userId, UUID messageId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.bytes = bytes;
    this.fileName = fileName;
    this.mimeType = mimeType;
    this.contentType = contentType;
    this.userId = userId;
    this.messageId = messageId;
  }

  public static BinaryContent createProfileImage(byte[] bytes, String fileName, String mimeType,
      UUID userId) {
    Objects.requireNonNull(userId, "컨텐트 생성 시 유저 id는 필수입니다.");
    return BinaryContent.builder()
        .bytes(Objects.requireNonNull(bytes))
        .fileName(Objects.requireNonNull(fileName))
        .mimeType(Objects.requireNonNull(mimeType))
        .contentType(ContentType.PROFILE_IMAGE)
        .userId(userId)
        .messageId(null)
        .build();
  }

  public static BinaryContent createMessageAttachment(byte[] bytes, String fileName,
      String mimeType,
      UUID messageId) {
    return BinaryContent.builder()
        .bytes(Objects.requireNonNull(bytes))
        .fileName(Objects.requireNonNull(fileName))
        .mimeType(Objects.requireNonNull(mimeType))
        .contentType(ContentType.MESSAGE_ATTACHMENT)
        .userId(null)
        .messageId(messageId)
        .build();
  }

  public void attachToMessage(UUID messageId) {
    if (this.messageId != null) {
      throw new IllegalStateException("이미 메시지에 연결된 파일입니다.");
    }
    this.messageId = Objects.requireNonNull(messageId, "메시지 ID는 null일 수 없습니다.");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BinaryContent binaryContent)) {
      return false;
    }
    return Objects.equals(id, binaryContent.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}