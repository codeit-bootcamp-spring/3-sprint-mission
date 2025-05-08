package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UpdateProfileRequest(UUID userId,
                                   UUID newProfiledId) {
}
