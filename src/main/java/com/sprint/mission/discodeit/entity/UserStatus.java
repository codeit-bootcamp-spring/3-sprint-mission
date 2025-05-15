package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

// 사용자별 마지막 접속 시간( last+5 : 온라인 ) / 판별 메서드 정의
@Getter
public class UserStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    //
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;
    private Instant lastOnlineAt;

    // 생성자
    public UserStatus(UUID userId, Instant lastOnlineAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userId = userId;
        this.lastOnlineAt = lastOnlineAt;
    }

    // Update 메서드
    public void update(Instant lastOnlineAt) {
        boolean updated = false;
        if (lastOnlineAt != null && lastOnlineAt.equals(this.lastOnlineAt)) {
            this.lastOnlineAt = lastOnlineAt;
            updated = true;
        }

        if (updated) {
            this.updatedAt = Instant.now();
        }
    }

    // 온라인 상태 판별 메서드 정의
    public boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

        return lastOnlineAt.isAfter(instantFiveMinutesAgo);
    }
}
