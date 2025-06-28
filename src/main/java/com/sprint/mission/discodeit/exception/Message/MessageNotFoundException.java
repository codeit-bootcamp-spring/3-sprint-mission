package com.sprint.mission.discodeit.exception.Message;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MessageNotFoundException extends MessageException {

    public final UUID messageId;

    public MessageNotFoundException(UUID messageId) {
        super(
                ErrorCode.MESSAGE_NOT_FOUND,
                Map.of("messageId", messageId)
        );
        this.messageId = messageId;
    }
}
