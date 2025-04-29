package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.model.ImmutableAuditable;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델
 * <p>
 * 수정 불가능한 도메인 모델로 간주하므로 updatedAt 필드는 정의하지 않음
 * <p>
 * <ul>
 * <li>AuditInfo (id, createdAt)</li>
 * <li>바이너리 데이터</li>
 * <li>파일명</li>
 * <li>MIME 타입</li>
 * </ul>
 */
@Getter
@ToString(callSuper = true)
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
public class BinaryContent extends ImmutableAuditable implements Serializable {

  @Serial
  private static final long serialVersionUID = 8121899659000317030L;

  public enum ContentType {
    PROFILE_IMAGE,          // userId 필수, messageId null
    MESSAGE_ATTACHMENT      // messageId 필수, userId null
  }

  // 바이너리 데이터 정보
  private final byte[] data;
  private final String fileName;
  private final String mimeType;

  // 참조 정보
  private final ContentType contentType;
  private final UUID userId;
  private final UUID messageId;

  private BinaryContent(byte[] data, String fileName, String mimeType, ContentType contentType,
      UUID userId, UUID messageId) {
    this.data = data;
    this.fileName = fileName;
    this.mimeType = mimeType;
    this.contentType = contentType;
    this.userId = userId;
    this.messageId = messageId;
  }

  public static BinaryContent createProfileImage(byte[] data, String fileName, String mimeType,
      UUID userId) {
    Objects.requireNonNull(userId, "컨텐트 생성 시 유저 id는 필수입니다.");
    return BinaryContent.builder()
        .data(Objects.requireNonNull(data))
        .fileName(Objects.requireNonNull(fileName))
        .mimeType(Objects.requireNonNull(mimeType))
        .contentType(ContentType.PROFILE_IMAGE)
        .userId(userId)
        .messageId(null)
        .build();
  }

  public static BinaryContent createMessageAttachment(byte[] data, String fileName, String mimeType,
      UUID messageId) {
    Objects.requireNonNull(messageId, "컨텐트 생성 시 메시지 id는 필수입니다.");
    return BinaryContent.builder()
        .data(Objects.requireNonNull(data))
        .fileName(Objects.requireNonNull(fileName))
        .mimeType(Objects.requireNonNull(mimeType))
        .contentType(ContentType.MESSAGE_ATTACHMENT)
        .userId(null)
        .messageId(messageId)
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BinaryContent)) {
      return false;
    }
    BinaryContent that = (BinaryContent) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}