package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.common.Period;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
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


    public boolean isOnline(Instant now) {
        return now.isAfter(lastActivityAt.plusSeconds(300));
    }
}
