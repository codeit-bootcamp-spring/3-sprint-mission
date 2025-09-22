package com.sprint.mission.discodeit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    TokenProperties accessToken,
    TokenProperties refreshToken
) {

    public record TokenProperties(
        String secret,
        long exp // milliseconds
    ) {

    }
}