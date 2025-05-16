package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * 유저 조회 응답 DTO
 */

public record UserCreateDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String name,
        String userEmail,
        UUID profileId
) { }