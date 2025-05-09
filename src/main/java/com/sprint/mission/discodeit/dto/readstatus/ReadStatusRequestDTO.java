package com.sprint.mission.discodeit.dto.readstatus;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusRequestDTO(UUID userId, UUID channelId, Instant lastReadTime) {

    public static ReadStatus toEntity(ReadStatusRequestDTO readStatusRequestDTO) {
        UUID userId = readStatusRequestDTO.userId();
        UUID channelId = readStatusRequestDTO.channelId();
        Instant lastReadTime = readStatusRequestDTO.lastReadTime();

        return new ReadStatus(userId, channelId, lastReadTime);
    }
}
