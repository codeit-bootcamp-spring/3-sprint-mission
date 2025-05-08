package com.sprint.mission.discodeit.service.dto.AuthLogin;

import lombok.Builder;

public record AuthLoginRequest(
        String userName,
        String password
) {
    @Builder
    public AuthLoginRequest {
    }
}
