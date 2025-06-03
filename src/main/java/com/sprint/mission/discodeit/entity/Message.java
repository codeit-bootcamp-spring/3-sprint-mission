package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
public class Message extends BaseUpdatableEntity {
    private String content;
    //
    private Channel channel;
    private User author;
    private List<BinaryContent> attachments;

    public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
        this.content = Objects.requireNonNull(content, "Message content must not be null");
        this.channel = Objects.requireNonNull(channel, "Channel ID must not be null");
        this.author = Objects.requireNonNull(author, "Author ID must not be null");
        this.attachments = (attachments != null) ? attachments : new ArrayList<>();
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
                ", user='" + getAuthor() + '\'' +
                ", channel='" + getChannel() + '\'' +
                ", id='" + getId() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}
