package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.response.UserResponse;

public record UserUpdatedEvent(UserResponse user) {

}
