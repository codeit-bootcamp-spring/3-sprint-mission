package com.sprint.mission.discodeit.event.payload;

import java.util.UUID;

public record UserLogoutEvent(UUID userId) {
}