package com.sprint.mission.discodeit.service.DTO.Request;

public record UserUpdateRequest (
        String userName,
        String email,
        String password
){}
