package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter // getter 대체
public class ReadStatus {  // 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    // 사용자가 채널을 마지막으로 확인한 시간을 나타내는 용도기에 user,channel만 Id 선언
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;


    // 생성자
    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void update(Instant lastReadAt) {
        boolean anyValueUpdated = false;
        if(lastReadAt != null && !lastReadAt.equals(this.lastReadAt)){
            this.lastReadAt = lastReadAt;
            anyValueUpdated = true;
        }

        if(anyValueUpdated){
            this.updatedAt = Instant.now();
        }

    }

}
