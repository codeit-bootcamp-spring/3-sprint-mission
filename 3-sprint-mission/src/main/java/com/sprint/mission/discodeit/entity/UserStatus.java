package com.sprint.mission.discodeit.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Data
@Getter
@ToString
public class UserStatus {
    private final UUID id;
    private final UUID userId;
    private boolean isLogin;
    private Instant lastLoginTime;
    private final Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.isLogin = true;
        this.lastLoginTime = null;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void update(Instant lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public boolean isLogin() {
        boolean isLogin =
                this.lastLoginTime.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
        this.updatedAt = Instant.now();
        return isLogin;

    }

}
