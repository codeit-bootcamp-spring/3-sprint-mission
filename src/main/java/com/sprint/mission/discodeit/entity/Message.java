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

/**
 * 메시지 정보 관리
 * <p>
 * <ul>
 *   <li>AuditInfo (id, createdAt, updatedAt)</li>
 *   <li>메시지 내용</li>
 *   <li>생성자 id</li>
 *   <li>채널 id</li>
 *   <li>삭제 여부</li>
 * </ul>
 */
@Getter
@ToString
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
public class Message implements Serializable {

  @Serial
  private static final long serialVersionUID = 5091331492371241399L;

  // 메시지 정보 관리
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  private String content;

  // 참조 정보
  private final UUID userId;
  private final UUID channelId;
  private Instant deletedAt;

  // 정적 팩토리 메서드로 명시적인 생성
  public static Message create(String content, UUID userId, UUID channelId) {
    return Message.builder()
        .id(UUID.randomUUID())
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .content(content)
        .userId(userId)
        .channelId(channelId)
        .deletedAt(null)
        .build();
  }

  public void setUpdatedAt() {
    this.updatedAt = Instant.now();
  }

  public String getContent() {
    return deletedAt != null ? "삭제된 메시지입니다." : content;
  }

  public void updateContent(String content) {
    if (deletedAt == null) {
      this.content = content;
      setUpdatedAt();
    }
  }

  public void delete() {
    if (deletedAt == null) {
      deletedAt = Instant.now();
      setUpdatedAt();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Message message = (Message) o;
    return Objects.equals(id, message.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}