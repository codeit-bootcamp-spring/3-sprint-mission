package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.time.Instant;

public record UserEvent (
    String eventName,
    UserDto data,
    Instant createdAt) {

}
