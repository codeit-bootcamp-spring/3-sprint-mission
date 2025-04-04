package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {

    // 공통 필드: 객체 id, 객체 생성시간, 객체 수정시간
    // 선택 필드: 채널 id,
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String channelName;

    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.channelName = name;
    }

    public UUID getId() { return id; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public String getName() { return channelName; }

    public void setChannelName(String name) {
        this.channelName = name;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "[Channel] {" + channelName + " id=" +  id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "}";
    }
}
