package com.sprint.mission.discodeit.dto.auth;

import com.sprint.mission.discodeit.entity.Role;

import java.util.UUID;

/**
 * PackageName  : com.sprint.mission.discodeit.dto.auth
 * FileName     : UserRoleUpdateRequest
 * Author       : dounguk
 * Date         : 2025. 8. 6.
 */
public record UserRoleUpdateRequest(
    UUID userId,
    Role newRole
) {
}
