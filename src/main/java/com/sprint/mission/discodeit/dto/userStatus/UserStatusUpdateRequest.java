package com.sprint.mission.discodeit.dto.userStatus;

import java.time.LocalDateTime;

public record UserStatusUpdateRequest(
    LocalDateTime newLastActiveAt) {
}
