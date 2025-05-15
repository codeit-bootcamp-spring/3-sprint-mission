package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    // 필드 정의
    private final UUID channelId;
    private final Instant createdAt;
    private Instant updatedAt;
    private String channelName;                          // 채널명
    private ChannelType channelType;                         // 채널 유형
    private String description;                           // 분류


    // 생성자
    public Channel(ChannelType channelType, String channelName, String description) {
        this.channelId = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.channelType = channelType;
        this.channelName = channelName;
        this.description = description;
    }


    // Update
    public void update(String newChannelName, String newDescription) {
        boolean updated = false;
        if (newChannelName != null && !newChannelName.equals(this.channelName)) {
            this.channelName = newChannelName;
            updated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
        }
        if (updated) {
            this.updatedAt = Instant.now();
        } else {
            throw new IllegalArgumentException("No field to update");
        }
    }
}
