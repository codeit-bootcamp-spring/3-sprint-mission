package com.sprint.mission.discodeit.auth.jwt.dto;

import com.sprint.mission.discodeit.dto.response.UserDto;

public record JwtDto(
    UserDto userDto,
    String accessToken
) { }
