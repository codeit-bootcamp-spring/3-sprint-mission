package com.sprint.mission.discodeit.service.dto.AuthLogin;

import java.util.UUID;

public record AuthLoginRequest(
        UUID id,
        String userName,
        String password
) {

}
