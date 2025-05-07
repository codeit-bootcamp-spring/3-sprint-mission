package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


// 사용자가 해당 채널에서 마지막으로 언제 읽었는지 확인
@Getter
public class ReadStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    //
    private final UUID readId;
    private final Instant createdAt;
    private Instant updatedAt;
    //
    private UUID userId;
    private UUID channelId;
    // 마지막으로 읽은 시간이 적합한가, 마지막으로 읽은 메세지가 적합한가.. << ERD 구조상 읽은 시간으로
    private Instant lastReadAt;

    // 생성자
    public ReadStatus(UUID userId, UUID channelId) {
        this.readId = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.userId = userId;
        this.channelId = channelId;
    }

    // Update 메서드
    public void update() {
        this.updatedAt = Instant.now();
        this.lastReadAt = Instant.now();
    }
}
