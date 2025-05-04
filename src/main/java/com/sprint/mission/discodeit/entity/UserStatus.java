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
    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
    }

    // Update 메서드
    public void update() {
        this.updatedAt = Instant.now();
        this.lastOnlineAt = Instant.now();
    }

    // 온라인 상태 판별 메서드 정의
    public boolean isOnline() {
        return lastOnlineAt != null &&
                // 마지막 접속 시간과 현재 시간의 간격이 5분 이내
                Duration.between(lastOnlineAt, Instant.now()).toMinutes() <= 5;
    }
}
