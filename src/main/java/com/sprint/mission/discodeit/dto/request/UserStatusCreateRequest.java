package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public final class UserStatusCreateRequest {
    private UUID userId;
    Instant lastOnlineAt;
}
