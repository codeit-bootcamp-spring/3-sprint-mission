package com.sprint.mission.discodeit.dto.AuthLogin;

import lombok.Builder;

public record LoginRequest(
        String username,
        String password
) {
    @Builder
    public LoginRequest {
    }
}
