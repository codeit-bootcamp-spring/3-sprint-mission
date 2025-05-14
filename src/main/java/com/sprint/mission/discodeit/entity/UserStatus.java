package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.common.Period;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserStatus extends Period implements Serializable {

    private final UUID userId;
    private Instant lastActivityAt;

    public UserStatus(UUID userId, Instant lastActivityAt) {
        super();
        this.userId = userId;
        this.lastActivityAt = lastActivityAt;
    }

    public void update(Instant lastActiveAt) {
        boolean anyValueUpdated = false;
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActivityAt)) {
            this.lastActivityAt = lastActiveAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.lastActivityAt = Instant.now();
        }
    }

    public boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

        return lastActivityAt.isAfter(instantFiveMinutesAgo);
    }
}
