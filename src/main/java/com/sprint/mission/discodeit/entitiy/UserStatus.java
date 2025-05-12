package com.sprint.mission.discodeit.entitiy;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserStatus implements Serializable {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;
    private Boolean online;

    public UserStatus(UUID userId) {
        this.userId = userId;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now(); // updatedAt 초기화 추가
        this.online = true; // 초기 상태를 온라인으로 설정
    }

    public Boolean IsOnline(){
        if (updatedAt == null) {
            return false;
        }

        Instant now = Instant.now();
        if (now.minusSeconds(300).isAfter(updatedAt)) {
            return online = false;
        } else {
            return online = true;
        }

    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userId=" + userId +
                '}';
    }
}
