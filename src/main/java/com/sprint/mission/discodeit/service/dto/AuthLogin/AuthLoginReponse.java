package com.sprint.mission.discodeit.service.dto.AuthLogin;

import java.util.UUID;

public record AuthLoginReponse(
        UUID id,
        String userName,
        String email
) {
}
