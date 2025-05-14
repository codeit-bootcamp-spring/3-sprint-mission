package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Message;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class MessageRequestDTO {
    private String content;
    private UUID senderId;
    private UUID channelId;
    private List<UUID> attachmentIds;

    public MessageRequestDTO(String content, UUID senderId, UUID channelId) {
        this.content = content;
        this.senderId = senderId;
        this.channelId = channelId;
        this.attachmentIds = new ArrayList<>();
    }

    public static Message toEntity(MessageRequestDTO messageRequestDTO) {
        Message message = new Message(messageRequestDTO.getSenderId(), messageRequestDTO.getChannelId());

        message.updateContent(messageRequestDTO.getContent());
        message.updateAttachmentIds(messageRequestDTO.getAttachmentIds());

        return message;
    }
}
