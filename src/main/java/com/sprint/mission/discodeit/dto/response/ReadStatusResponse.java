package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    UUID userId,
    UUID channelId,
    Instant lastReadAt
) {

    public static ReadStatusResponse fromEntity(ReadStatus rs) {
        return new ReadStatusResponse(
            rs.getId(),
            rs.getCreatedAt(),
            rs.getUpdatedAt(),
            rs.getUserId(),
            rs.getChannelId(),
            rs.getLastReadAt()
        );
    }
}
