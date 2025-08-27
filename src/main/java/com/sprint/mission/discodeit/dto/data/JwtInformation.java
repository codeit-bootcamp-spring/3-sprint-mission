package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record JwtInformation(
    UUID userId,
    String accessToken,
    String refreshToken,
    Instant accessTokenExpiresAt,
    Instant refreshTokenExpiresAt,
    String role
) {}