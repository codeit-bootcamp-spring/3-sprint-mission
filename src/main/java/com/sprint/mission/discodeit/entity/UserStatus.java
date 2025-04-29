package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class UserStatus {
    @Getter
    private final UUID id;
    @Getter
    private final Instant createdAt;
    @Getter
    private Instant updatedAt;
    //
    @Getter
    private final UUID userId;
    //
    @Getter
    private UserStatusType status;
    @Getter
    private Instant lastOnlineTime;


    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = UserStatusType.ONLINE;
        this.lastOnlineTime = Instant.now();
        //
        this.userId = userId;
    }

    public void update(UserStatusType newStatus) {
        boolean anyValueUpdated = false;
        if (newStatus != null && newStatus != this.status) {
            // 이전 상태가 온라인이였고 현재가 온라인이 아닐때, 바뀌는 시점에 lastOnlineTime를 현재시간으로 업데이트 해줘야함.
            if (this.status.equals(UserStatusType.ONLINE)) {
                this.lastOnlineTime = Instant.now();
            }

            this.status = newStatus;

            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String createdAtFormatted = formatter.format(createdAt);
        String updatedAtFormatted = formatter.format(updatedAt);

        return "🙋‍♂️ UserStatus {\n" +
                "  id         = " + id + "\n" +
                "  createdAt  = " + createdAtFormatted + "\n" +
                "  updatedAt  = " + updatedAtFormatted + "\n" +
                "  userId       = '" + userId + "'\n" +
                "  status       = '" + status + "'\n" +
                "  lastOnlineTime       = '" + lastOnlineTime + "'\n" +
                "}";
    }
}
