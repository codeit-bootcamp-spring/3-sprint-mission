package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;

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
public class Message implements Serializable {

  @Serial
  private static final long serialVersionUID = 5091331492371241399L;
  // 메시지 정보 관리
  private final UUID id;
  private final long createdAt;
  private long updatedAt;
  private String content;
  // 참조 정보 getter
  private final UUID userId;
  private final UUID channelId;
  private Long deletedAt;

  // 외부에서 직접 객체 생성 방지.
  private Message(String content, UUID userId, UUID channelId) {
    this.id = UUID.randomUUID();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
    this.content = content;
    this.userId = userId;
    this.channelId = channelId;
  }

  // 정적 팩토리 메서드로 명시적인 생성
  public static Message create(String content, UUID userId, UUID channelId) {
    return new Message(content, userId, channelId);
  }

  public void setUpdatedAt() {
    this.updatedAt = System.currentTimeMillis();
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
      deletedAt = System.currentTimeMillis();
      setUpdatedAt();
    }
  }

  @Override
  public String toString() {
    return "Message{" +
        "id=" + id +
        ", createdAt=" + createdAt +
        ", updatedAt=" + updatedAt +
        ", content='" + content + '\'' +
        ", userId=" + userId +
        ", channelId=" + channelId +
        '}';
  }
}