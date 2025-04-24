package com.sprint.mission.discodeit.entitiy;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds;
    private String text;
    private BinaryContent binaryContent;

    public Message(UUID channelId, UUID authorId, List<UUID> attachmentIds, String text, BinaryContent binaryContent) {
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds;
        this.text = text;
        this.binaryContent = binaryContent;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelId=" + channelId +
                ", authorId=" + authorId +
                ", attachmentIds=" + attachmentIds +
                ", text='" + text + '\'' +
                ", binaryContent=" + binaryContent +
                '}';
    }
}
