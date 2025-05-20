package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record LoginRequest(
                           String username,
                           String password) {
}
