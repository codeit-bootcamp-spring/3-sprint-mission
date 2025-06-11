package com.sprint.mission.discodeit.dto.User;

import lombok.Builder;

@Builder
public record UserCreateRequest(
        String username,
        String email,
        String password
) {
}