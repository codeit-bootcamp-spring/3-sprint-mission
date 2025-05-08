package com.sprint.mission.discodeit.dto.AuthLogin;

import java.util.UUID;
import lombok.Builder;

public record AuthLoginRequest(
        UUID id,
        String userName,
        String password
) {
    @Builder
    public AuthLoginRequest {
    }
}
