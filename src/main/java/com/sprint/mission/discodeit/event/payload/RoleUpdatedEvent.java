package com.sprint.mission.discodeit.event.payload;

import java.util.UUID;

public record RoleUpdatedEvent(
        UUID userId,
        String oldRole,
        String newRole
) {
}