package com.sprint.mission.discodeit.dto.readstatus;

import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusRequestDTO {

    private UUID userId;
    private UUID channelId;
    private Instant lastReadTime;

    public ReadStatusRequestDTO(UUID userId, UUID channelId, Instant lastReadTime) {
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadTime = lastReadTime;
    }

    public static ReadStatus toEntity(ReadStatusRequestDTO readStatusRequestDTO) {
        UUID userId = readStatusRequestDTO.getUserId();
        UUID channelId = readStatusRequestDTO.getChannelId();
        Instant lastReadTime = readStatusRequestDTO.getLastReadTime();

        return new ReadStatus(userId, channelId, lastReadTime);
    }
}
