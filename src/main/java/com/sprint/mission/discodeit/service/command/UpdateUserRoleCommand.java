package com.sprint.mission.discodeit.service.command;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;

public record UpdateUserRoleCommand(UUID userId, Role newRole) {}
