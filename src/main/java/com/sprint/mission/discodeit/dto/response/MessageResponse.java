package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UUID authorId,
    List<UUID> attachmentIds
) {

    public static MessageResponse fromEntity(Message msg) {
        return new MessageResponse(
            msg.getId(),
            msg.getCreatedAt(),
            msg.getUpdatedAt(),
            msg.getContent(),
            msg.getChannelId(),
            msg.getAuthorId(),
            msg.getAttachmentIds()
        );
    }
}
