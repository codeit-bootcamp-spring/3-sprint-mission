package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Message;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record MessageDTO(UUID id,
                         Instant creatdAt,
                         Instant updatedAt,
                         String content,
                         UUID authorId,
                         UUID channelId,
                         List<UUID> attachmentIds) {

    public static MessageDTO fromDomain(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .creatdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .content(message.getContent())
                .authorId(message.getAuthorId())
                .channelId(message.getChannelId())
                .attachmentIds(message.getAttachmentIds())
                .build();
    }
}
