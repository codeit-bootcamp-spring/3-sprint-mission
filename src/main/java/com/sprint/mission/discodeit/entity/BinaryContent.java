package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델
 * <p>
 * 수정 불가능한 도메인 모델로 간주하므로 updatedAt 필드는 정의하지 않음
 * <p>
 * <ul>
 *   <li>AuditInfo (id, createdAt)</li>
 *   <li>바이너리 데이터</li>
 *   <li>파일명</li>
 *   <li>MIME 타입</li>
 * </ul>
 */
@Getter
@Builder
public class BinaryContent implements Serializable {

  @Serial
  private static final long serialVersionUID = 8121899659000317030L;

  public enum ContentType {
    PROFILE_IMAGE,
    MESSAGE_ATTACHMENT
  }

  // 공통 정보
  private final UUID id;
  private final Instant createdAt;

  // 바이너리 데이터 관련 정보
  private final byte[] data;
  private final String fileName;
  private final String mimeType;

  // 참조 정보
  private final ContentType contentType;
  private final UUID userId;
  private final UUID messageId;

  // 정적 팩토리 메서드
  public static BinaryContent createProfileImage(byte[] data, String fileName, String mimeType,
      UUID userId) {
    return builder()
        .data(data)
        .fileName(fileName)
        .mimeType(mimeType)
        .contentType(ContentType.PROFILE_IMAGE)
        .userId(userId)
        .messageId(null)
        .id(UUID.randomUUID())
        .createdAt(Instant.now())
        .build();
  }

  public static BinaryContent createMessageAttachment(byte[] data, String fileName, String mimeType,
      UUID messageId) {
    return builder()
        .data(data)
        .fileName(fileName)
        .mimeType(mimeType)
        .contentType(ContentType.MESSAGE_ATTACHMENT)
        .userId(null)
        .messageId(messageId)
        .id(UUID.randomUUID())
        .createdAt(Instant.now())
        .build();
  }

  @Override
  public String toString() {
    return "BinaryContent{" +
        "id=" + id +
        ", createdAt=" + createdAt +
        ", fileName='" + fileName + '\'' +
        ", mimeType='" + mimeType + '\'' +
        ", contentType=" + contentType +
        ", userId=" + userId +
        ", messageId=" + messageId +
        ", dataSize=" + (data != null ? data.length : 0) + " bytes" +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BinaryContent binaryContent = (BinaryContent) o;
    return Objects.equals(id, binaryContent.id) &&
        Objects.equals(fileName, binaryContent.fileName) &&
        Objects.equals(mimeType, binaryContent.mimeType) &&
        Objects.equals(createdAt, binaryContent.createdAt) &&
        Objects.deepEquals(data, binaryContent.data) &&
        contentType == binaryContent.contentType &&
        Objects.equals(userId, binaryContent.userId) &&
        Objects.equals(messageId, binaryContent.messageId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, fileName, mimeType, createdAt, Objects.hashCode(data), contentType,
        userId, messageId);
  }
}
