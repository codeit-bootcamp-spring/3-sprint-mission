package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record CreateReadStatusRequest(UUID userId, UUID channelId) {
}
