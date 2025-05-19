package com.sprint.mission.discodeit.dto.User;

public record UserCreateRequest(
        String username,
        String email,
        String password
) {

}

