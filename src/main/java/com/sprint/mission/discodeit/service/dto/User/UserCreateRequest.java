package com.sprint.mission.discodeit.service.dto.User;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        boolean content,
        byte[] profileImage,
        String profileContentType
) {
}
