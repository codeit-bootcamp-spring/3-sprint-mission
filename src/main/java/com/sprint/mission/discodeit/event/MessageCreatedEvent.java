package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import java.util.UUID;

public record MessageCreatedEvent(
    MessageDto messageDto,
    UUID channelId,
    UUID messageId
) {

}
