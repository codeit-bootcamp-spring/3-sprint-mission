package com.sprint.mission.discodeit.dto.User;

import lombok.Builder;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        boolean content,
        byte[] profileImage,
        String profileContentType
) {
    @Builder
    public UserCreateRequest {
    }
}
