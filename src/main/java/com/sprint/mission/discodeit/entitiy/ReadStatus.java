package com.sprint.mission.discodeit.entitiy;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ReadStatus {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;
    private UUID channelId;
    private Instant readAt;

    public ReadStatus(UUID userId, UUID channelId, Instant readAt) {
        this.userId = userId;
        this.channelId = channelId;
        this.readAt = readAt;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }



    @Override
    public String toString() {
        return "ReadStatus{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", readAt=" + readAt +
                '}';
    }
}
