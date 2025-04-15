package com.sprint.mission.discodeit.entity;


import java.time.Instant;
import java.util.UUID;

public class Message extends Base {

    private UUID authorId;
    private UUID channelId; // 이걸 Channel 클래스와 어떻게 연결시키지
    private String content; // message는 결국 HashSet이야


    public Message(String content, UUID channelId, UUID authorId) {
        super();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }


    // Getter Methods
    public String getContent() {
        return content;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }


    // Update Method

    public void updateMessage(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.updateUpdatedAt(Instant.now().getEpochSecond());
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + getContent() + '\'' +
                ", id='" + getId() + '\'' +
                ", channelId='" + getChannelId() + '\'' +
                ", authorId='" + getAuthorId() + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", updatedAt='" + getUpdatedAt() + '\'' +
                '}';
    }
}
