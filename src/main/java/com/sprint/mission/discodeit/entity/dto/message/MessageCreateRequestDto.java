package com.sprint.mission.discodeit.entity.dto.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class MessageCreateRequestDto {
    private UUID channelId;
    private UUID userId;
    private String messageContent;
    private List<byte[]> messageFile;

    public MessageCreateRequestDto(UUID channelId, UUID userId, String messageContent) {
        this.channelId = channelId;
        this.userId = userId;
        this.messageContent = messageContent;
    }

}
