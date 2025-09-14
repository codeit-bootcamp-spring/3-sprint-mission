package com.sprint.mission.discodeit.security.jwt.store;

import com.sprint.mission.discodeit.dto.data.UserDto;

public record JwtDto (
        UserDto userDto,
        String accessToken
) { }
