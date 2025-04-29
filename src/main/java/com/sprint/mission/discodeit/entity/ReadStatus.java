package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

/**
 * 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델
 * <p>
 * <ul>
 *   <li>AuditInfo (id, createdAt, updatedAt)</li>
 *   <li>사용자 ID</li>
 *   <li>채널 ID</li>
 *   <li>마지막으로 읽은 시간</li>
 * </ul>
 */
@Getter
@ToString
public class ReadStatus implements Serializable {

  @Serial
  private static final long serialVersionUID = -6861799438879244084L;

  // 공통 정보
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  // 참조 정보
  private final UUID userId;
  private final UUID channelId;
  private Instant lastReadAt;

  // 외부에서 직접 객체 생성 방지
  private ReadStatus(UUID userId, UUID channelId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = this.createdAt;
  }

  // 정적 팩토리 메서드로 명시적인 생성
  public static ReadStatus create(UUID userId, UUID channelId) {
    return new ReadStatus(userId, channelId);
  }

  public void setUpdatedAt() {
    this.updatedAt = Instant.now();
  }

  public void updateLastReadAt() {
    this.lastReadAt = Instant.now();
    setUpdatedAt();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReadStatus readStatus = (ReadStatus) o;
    return Objects.equals(id, readStatus.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}