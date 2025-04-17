package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * 메시지 정보 관리
 * <p>
 * 공통 속성(고유 아이디, 생성/수정 시간) 관리는 {@link Base} 객체에 위임하여 컴포지션 방식으로 구현한다.
 * <ul>
 *   <li>메시지 내용</li>
 *   <li>생성자 id</li>
 *   <li>채널 id</li>
 *   <li>삭제 여부</li>
 * </ul>
 */
public class Message implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private final Base base;
  private String content;
  private final UUID userId;
  private final UUID channelId;
  private boolean deleted = false;

  // 외부에서 직접 객체 생성 방지.
  private Message(String content, UUID userId, UUID channelId) {
    this.base = new Base();
    this.content = content;
    this.userId = userId;
    this.channelId = channelId;
  }

  // 정적 팩토리 메서드로 명시적인 생성
  public static Message create(String content, UUID userId, UUID channelId) {
    return new Message(content, userId, channelId);
  }

  // 메시지 내용 관리
  public String getContent() {
    return deleted ? "삭제된 메시지입니다." : content;
  }

  public void updateContent(String content) {
    if (!deleted) {
      this.content = content;
      base.setUpdatedAt();
    }
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void delete() {
    if (!deleted) {
      deleted = true;
      base.setUpdatedAt();
    }
  }

  // Base 위임 메서드
  public UUID getId() {
    return base.getId();
  }

  public long getCreatedAt() {
    return base.getCreatedAt();
  }

  public long getUpdatedAt() {
    return base.getUpdatedAt();
  }

  // 참조 정보 getter
  public UUID getUserId() {
    return userId;
  }

  public UUID getChannelId() {
    return channelId;
  }

  @Override
  public String toString() {
    return "Message{" +
        "id=" + getId() +
        ", createdAt=" + getCreatedAt() +
        ", updatedAt=" + getUpdatedAt() +
        ", content='" + content + '\'' +
        ", userId=" + userId +
        ", channelId=" + channelId +
        '}';
  }
}