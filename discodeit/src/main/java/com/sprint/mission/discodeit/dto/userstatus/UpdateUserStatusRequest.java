package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;

public record UpdateUserStatusRequest(Instant newLastActiveAt) {
}
