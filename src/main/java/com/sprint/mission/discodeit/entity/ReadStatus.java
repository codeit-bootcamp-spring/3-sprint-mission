package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
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
 * <li>AuditInfo (id, createdAt, updatedAt)</li>
 * <li>사용자 ID</li>
 * <li>채널 ID</li>
 * <li>마지막으로 읽은 시간</li>
 * </ul>
 */
@Getter
@ToString(callSuper = true)
public class ReadStatus extends Auditable implements Serializable {

  @Serial
  private static final long serialVersionUID = -6861799438879244084L;

  private Instant lastReadAt;

  // 참조 정보
  private final UUID userId;
  private final UUID channelId;

  // 외부에서 직접 객체 생성 방지
  private ReadStatus(UUID userId, UUID channelId) {
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = getCreatedAt();
  }

  // 정적 팩토리 메서드로 명시적인 생성
  public static ReadStatus create(UUID userId, UUID channelId) {
    ReadStatus readStatus = new ReadStatus(userId, channelId);
    readStatus.touch();
    return readStatus;
  }

  public static ReadStatus create(ReadStatusCreateRequest request) {
    ReadStatus readStatus = new ReadStatus(request.userId(), request.channelId());
    readStatus.touch();
    return readStatus;
  }

  public void updateLastReadAt() {
    this.lastReadAt = Instant.now();
    touch();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReadStatus that = (ReadStatus) o;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}