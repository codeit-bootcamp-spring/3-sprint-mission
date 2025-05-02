package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.common.Period;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ReadStatus extends Period implements Serializable {
    private final UUID userId;
    private final UUID channelId;
    private Instant joinedAt;

    public ReadStatus(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
        this.joinedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "ReadStatus{" +
                "channelId=" + channelId +
                ", userId=" + userId +
                ", joinedAt=" + joinedAt +
                "} " + super.toString();
    }
}
