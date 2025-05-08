package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
  private static final long serialVersionUID = 1L;
  private final UUID id;
  private final Instant createdAt;
  private Instant updatedAt;

  private UUID userId;
  private UUID channelId;
  private Instant lastReadAt;

  public ReadStatus(UUID userId, UUID channelId) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now() ;
    this.updatedAt = Instant.now();
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = Instant.EPOCH; // 기본값을 Instant.EPOCH로 메시지를 읽지 않음을 표시
  }

  /*
  사용자가 채널 별 마지막 메세지를 읽은 시간을 표현
  마지막으로 읽은 시간 업데이트 필요
  사용자가 각 채널에 읽지 않은 메세지를 확인위한 용도
  updateLastReadAt 호출 시점? 사용자가 채널에 접속할 때
   */
  public void updateLastReadAt(Instant currentReadAt) {
    if (currentReadAt.isAfter(this.lastReadAt)) {
      this.lastReadAt = currentReadAt;
      this.updatedAt = Instant.now();
    }
  }
}

