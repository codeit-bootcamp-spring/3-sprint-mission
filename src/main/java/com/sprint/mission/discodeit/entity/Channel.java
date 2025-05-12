package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class Channel implements Serializable {
    private static final long serialVersionUID = 79529852066494114L;

    /* 공통 필드 */
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private String channelName;     // 채널 이름
    private String description;     // 채널 설명
    private ChannelType type;       // 채널 타입

    @Builder
    public Channel(ChannelType type, String channelName, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();

        this.type = type;
        this.channelName = channelName;
        this.description = description;
    }

    public void update(String newChannelName, String newDescription) {
        boolean isUpdated = false;

        if (newChannelName != null && !newChannelName.equals(this.channelName)) {
            this.channelName = newChannelName;
            isUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            isUpdated = true;
        }
        if (isUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
