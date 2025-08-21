package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.dto.response.UserResponse;

public record JwtDto(
    UserResponse userDto,
    String accessToken
) {

}