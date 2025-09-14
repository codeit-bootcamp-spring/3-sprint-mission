package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import java.time.Instant;
import java.util.UUID;

public record RoleUpdatedEvent (
    UUID userId,
    Role oldRole,
    Role newRole,
    Instant occurredAt
) {
    public static RoleUpdatedEvent now(UUID userId, Role oldRole, Role newRole) {
        return new RoleUpdatedEvent(userId, oldRole, newRole, Instant.now());
    }
}