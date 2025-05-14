package com.sprint.mission.discodeit.entity.dto.message;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString
@Getter
public class MessageCreateRequestDto {
    private UUID channelId;
    private UUID userId;
    private String messageContent;
    private List<byte[]> messageFile = new ArrayList<>();

    public MessageCreateRequestDto(UUID channelId, UUID userId, String messageContent, List<byte[]> messageFile) {
        this.channelId = channelId;
        this.userId = userId;
        this.messageContent = messageContent;
        this.messageFile = messageFile;
    }

    public MessageCreateRequestDto(UUID channelId, UUID userId, String messageContent) {
        this.channelId = channelId;
        this.messageContent = messageContent;
        this.userId = userId;
    }
}
