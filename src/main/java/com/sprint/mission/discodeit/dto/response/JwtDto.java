package com.sprint.mission.discodeit.dto.response;

public record JwtDto(
    UserResponse user,
    String accessToken
) {

}