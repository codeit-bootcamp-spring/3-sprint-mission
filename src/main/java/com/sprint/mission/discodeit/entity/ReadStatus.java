package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;

@Getter
public class ReadStatus extends BaseUpdatableEntity {
    private User user;
    private Channel channel;
    private Instant lastReadAt;

    public ReadStatus(User user, Channel channel, Instant lastReadAt) {
        this.user = Objects.requireNonNull(user, "User ID must not be null");
        this.channel = Objects.requireNonNull(channel, "Channel ID must not be null");
        this.lastReadAt = Objects.requireNonNull(lastReadAt, "Last read time must not be null");
    }

    public void update(Instant newLastReadAt) {
        boolean anyValueUpdated = false;
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
