package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.message.MessageResponseDto;

public record MessageCreatedEvent(
    MessageResponseDto data
) {

}
