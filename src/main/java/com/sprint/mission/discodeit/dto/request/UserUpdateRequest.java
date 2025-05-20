package com.sprint.mission.discodeit.dto.request;

public record UserUpdateRequest(
        String userName,
        String email,
        String password
) {
}
