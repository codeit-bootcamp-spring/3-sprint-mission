package com.sprint.mission.discodeit.dto;

public record UserCreateDTO(
        String username,
        String email,
        String password,
        String name
) {
}
