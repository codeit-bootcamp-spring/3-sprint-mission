package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;

public record RoleUpdatedEvent(
        User user,
        Role oldRole,
        Instant occurredAt
) {
}
