package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UpdateUserStatusRequest(UUID id, UUID userId) {
}
