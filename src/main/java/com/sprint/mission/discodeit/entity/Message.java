package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class Message implements Serializable {
    @Getter
    private static final long serialVersionUID = 1L;
    //
    @Getter
    private final UUID id;
    @Getter
    private final Instant createdAt;
    @Getter
    private Instant updatedAt;
    //
    @Getter
    private String content;
    //
    @Getter
    private final UUID userId;
    @Getter
    private final UUID channelId;
    @Getter
    private List<UUID> attachmentIds;  // BinaryContent의 id


    public Message(MessageCreateRequest createRequest) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        //
        this.content = createRequest.content();
        this.userId = createRequest.userId();
        this.channelId = createRequest.channelId();
        this.attachmentIds = createRequest.attachmentIds();
    }

    //QUESTION. updateRequest도 안에 id를 포함할께 아니라 id, 변화될 필드 이렇게 나누는게 나을까?
    public void update(String content, List<UUID> attachmentIds) {
        boolean anyValueUpdated = false;
        if (content != null && !content.equals(this.content)) {
            this.content = content;
            anyValueUpdated = true;
        }

        if (attachmentIds != null && !attachmentIds.equals(this.attachmentIds)) {
            this.attachmentIds = attachmentIds;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String createdAtFormatted = formatter.format(createdAt);
        String updatedAtFormatted = formatter.format(updatedAt);

        return "💬 Message {\n" +
                " id         = " + id + "\n" +
                " createdAt  = " + createdAtFormatted + "\n" +
                " updatedAt  = " + updatedAtFormatted + "\n" +
                " content       = '" + content + "'\n" +
                " userId     = " + userId + "\n" +
                " channelId     = " + channelId + "\n" +
                " attachmentIds     = " + attachmentIds + "\n" +
                "}";
    }
}
