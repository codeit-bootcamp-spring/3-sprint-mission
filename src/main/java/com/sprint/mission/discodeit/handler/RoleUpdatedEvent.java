package com.sprint.mission.discodeit.handler;

import com.sprint.mission.discodeit.entity.Role;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.handler
 * FileName     : RoleUpdateEvent
 * Author       : dounguk
 * Date         : 2025. 9. 1.
 */
@Getter
public class RoleUpdatedEvent extends UpdatedEvent<Role> {

    private final UUID userId;

    public RoleUpdatedEvent(UUID userId, Role from, Role to, Instant updatedAt) {
        super(from, to, updatedAt);
        this.userId = userId;
    }
}