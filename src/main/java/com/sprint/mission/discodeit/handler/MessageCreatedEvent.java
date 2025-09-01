package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.dto.message.response.MessageResponse;

import java.time.Instant;

/**
 * PackageName  : com.sprint.mission.discodeit.handler
 * FileName     : MessageCreatedEvent
 * Author       : dounguk
 * Date         : 2025. 9. 1.
 */
public class MessageCreatedEvent extends CreatedEvent<MessageResponse> {
    public MessageCreatedEvent(MessageResponse data, Instant createdAt) {
        super(data, createdAt);
    }
}
