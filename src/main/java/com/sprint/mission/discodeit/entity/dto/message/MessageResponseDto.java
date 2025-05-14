package com.sprint.mission.discodeit.entity.dto.message;

import com.sprint.mission.discodeit.entity.Message;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class MessageResponseDto {
    private UUID userId;
    private String messageContent;
    private UUID messageId;
    private List<byte[]> messageFile;

    public MessageResponseDto(Message message, List<byte[]> messageFile) {
        this.userId = message.getSender();
        this.messageContent = message.getContent();
        this.messageId = message.getId();
        this.messageFile = messageFile;
    }

    public MessageResponseDto(Message message) {
        this.userId = message.getSender();
        this.messageContent = message.getContent();
        this.messageId = message.getId();
        this.messageFile = new ArrayList<>();
    }
}
