package com.sprint.mission.discodeit.entity.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
public class MessageUpdateRequestDto {

    private UUID messageId;
    private String messageContent;
    private UUID channelId;
    private UUID userId;
}
