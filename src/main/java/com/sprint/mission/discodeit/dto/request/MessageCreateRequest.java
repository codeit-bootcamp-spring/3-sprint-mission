package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;


@Getter
@AllArgsConstructor
public final class MessageCreateRequest {
    private final String messageContent;
    private final UUID channelId;
    private final UUID authorId;
}
