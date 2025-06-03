package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Message extends BaseUpdatableEntity {
    private String content;
    //
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds;

    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.content = Objects.requireNonNull(content, "Message content must not be null");
        this.channelId = Objects.requireNonNull(channelId, "Channel ID must not be null");
        this.authorId = Objects.requireNonNull(authorId, "Author ID must not be null");
        this.attachmentIds = (attachmentIds != null) ? attachmentIds : new ArrayList<>();
    }

    public void update(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + getContent() + '\'' +
                ", user='" + getAuthorId() + '\'' +
                ", channel='" + getChannelId() + '\'' +
                ", id='" + getId() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}
