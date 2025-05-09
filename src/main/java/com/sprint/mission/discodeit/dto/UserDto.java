package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserDto(UUID userId, String name, String email, UUID profileId,
                      Boolean online) {
}
