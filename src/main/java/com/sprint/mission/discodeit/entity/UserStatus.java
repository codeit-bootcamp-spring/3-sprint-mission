package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;

@Getter
public class UserStatus extends BaseUpdatableEntity {
    private User user;
    private Instant lastActiveAt;

    public UserStatus(User user, Instant lastActiveAt) {
        this.user = Objects.requireNonNull(user, "User ID must not be null");
        this.lastActiveAt = Objects.requireNonNull(lastActiveAt, "Last active time must not be null");
    }

    public void update(Instant lastActiveAt) {
        boolean anyValueUpdated = false;
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    /**
     * 마지막 접속 시간이 현재 시간 기준 5분 이내이면 true
     */
    public Boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

        return lastActiveAt.isAfter(instantFiveMinutesAgo);
    }
}
