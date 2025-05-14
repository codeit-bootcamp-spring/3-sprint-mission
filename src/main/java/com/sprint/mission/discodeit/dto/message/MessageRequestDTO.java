package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public record MessageRequestDTO(String content, UUID senderId, UUID channelId, List<UUID> attachmentIds) {

    public static Message toEntity(MessageRequestDTO messageRequestDTO) {
        Message message = new Message(messageRequestDTO.senderId(), messageRequestDTO.channelId(),
                messageRequestDTO.content(),
                messageRequestDTO.attachmentIds);

        return message;
    }
}
