package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.response.MessageResponse;

public record MessageCreatedEvent(MessageResponse message) {

}
