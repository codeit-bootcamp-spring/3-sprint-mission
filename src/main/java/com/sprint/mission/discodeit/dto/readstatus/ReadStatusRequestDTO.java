package com.sprint.mission.discodeit.dto.readstatus;

import com.sprint.mission.discodeit.entity.ReadStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Message 읽음 상태 생성 및 수정 정보")
public record ReadStatusRequestDTO(UUID userId, UUID channelId, Instant lastReadAt) {

  public static ReadStatus toEntity(ReadStatusRequestDTO readStatusRequestDTO) {
    UUID userId = readStatusRequestDTO.userId();
    UUID channelId = readStatusRequestDTO.channelId();
    Instant lastReadAt = readStatusRequestDTO.lastReadAt();

    return new ReadStatus(userId, channelId, lastReadAt);
  }
}
