package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
public class UserStatus {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;
    private Instant lastLoginAt;

    public UserStatus(User user) {

        this.id = UUID.randomUUID();
//        this.createdAt = Instant.ofEpochSecond(Instant.now().getEpochSecond());
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        this.updatedAt = this.createdAt;
        //
        this.userId = user.getId();

        this.lastLoginAt = this.createdAt;

    }

    public boolean isCurrenlyLoggedIn() {
        return lastLoginAt.plusSeconds(300).isAfter(Instant.now()); // true: 로그인
    }

    public void update() {
        boolean anyValueUpdated = false;
        if (this.isCurrenlyLoggedIn()) {
            this.lastLoginAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.ofEpochSecond(Instant.now().getEpochSecond());
        }
    }
}
