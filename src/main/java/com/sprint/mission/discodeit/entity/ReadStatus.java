package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDTO;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용
 */
@Getter
public class ReadStatus implements Serializable {

  private static final long serialVersionUID = 1L;
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;
  private final UUID userId;
  private final UUID channelId;
  private Instant lastReadAt;

  public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = lastReadAt;
  }

  public void updateLastReadAt(Instant lastReadAt) {
    this.updatedAt = Instant.now();
    this.lastReadAt = lastReadAt;
  }

  public static ReadStatusResponseDTO toDTO(ReadStatus readStatus) {
    ReadStatusResponseDTO readStatusResponseDTO = new ReadStatusResponseDTO(readStatus.getId(),
        readStatus.getCreatedAt(),
        readStatus.getUpdatedAt(),
        readStatus.getUserId(),
        readStatus.getChannelId(),
        readStatus.getLastReadAt());

    return readStatusResponseDTO;
  }

  @Override
  public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) {
          return false;
      }
    ReadStatus that = (ReadStatus) o;
    return Objects.equals(id, that.id) && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(userId, that.userId)
        && Objects.equals(channelId, that.channelId) && Objects.equals(lastReadAt, that.lastReadAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, channelId);
  }
}
