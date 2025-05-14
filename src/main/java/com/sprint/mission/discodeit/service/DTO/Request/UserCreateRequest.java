package com.sprint.mission.discodeit.service.DTO.Request;

public record UserCreateRequest(
        String username,
        String email,
        String password
) {}
