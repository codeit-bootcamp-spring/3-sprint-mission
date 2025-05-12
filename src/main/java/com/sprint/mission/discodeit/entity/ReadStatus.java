package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = -2967695321160578904L;

    /* 공통 필드 */
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;            // 유저ID
    private UUID channelId;         // 채널ID
    private Instant lastReadAt;     // 메시지를 마지막으로 읽은 시간

    @Builder
    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void update(Instant lastReadAt) {
        this.lastReadAt = lastReadAt != null ? lastReadAt : Instant.now();
        this.updatedAt = Instant.now();
    }
}
