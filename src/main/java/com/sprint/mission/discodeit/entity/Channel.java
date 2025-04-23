package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

// 채널
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    // 필드 정의
    private final UUID channelId;
    private final Long createdAt;
    private Long updatedAt;
    private String channelName;                          // 채널명
    private ChannelType channelType;                         // 채널 유형
    private String category;                           // 분류


    // 생성자
    public Channel(ChannelType channelType, String channelName, String category) {
        this.channelId = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();

        this.channelType = channelType;
        this.channelName = channelName;
        this.category = category;
    }

    // Getter

    public UUID getChannelId() {
        return channelId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getChannelName() {
        return channelName;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public String getCategory() {
        return category;
    }


    // Update
    public void update(String newChannelName, String newCategory) {
        boolean updated = false;
        if (newChannelName != null && !newChannelName.equals(this.channelName)) {
            this.channelName = newChannelName;
            updated = true;
        }
        if (newCategory != null && !newCategory.equals(this.category)) {
            this.category = newCategory;
        }
        if (updated) {
            this.updatedAt = System.currentTimeMillis();
        } else {
            throw new IllegalArgumentException("No field to update");
        }
    }
}
