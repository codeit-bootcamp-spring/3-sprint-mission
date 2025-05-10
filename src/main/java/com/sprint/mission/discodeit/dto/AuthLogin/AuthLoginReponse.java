package com.sprint.mission.discodeit.dto.AuthLogin;

import java.util.UUID;
import lombok.Builder;

@Builder
public record AuthLoginReponse(
        UUID id,
        String userName,
        String email
) {
}
