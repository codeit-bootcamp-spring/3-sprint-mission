package com.sprint.mission.discodeit.entitiy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ReadStatus implements Serializable {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;
    private UUID channelId;

    public ReadStatus(UUID userId, UUID channelId ) {
        this.userId = userId;
        this.channelId = channelId;
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
                '}';
    }
}
