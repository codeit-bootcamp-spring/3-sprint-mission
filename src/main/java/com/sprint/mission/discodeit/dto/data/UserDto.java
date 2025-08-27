package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Role;

import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    BinaryContentDto profile,
    Boolean online,
    Role role
) {
    public static UserDto withOnlineStatus(UserDto userDto, boolean online) {
        return new UserDto(
                userDto.id(),
                userDto.username(),
                userDto.email(),
                userDto.profile(),
                online,
                userDto.role()
        );
    }
}
