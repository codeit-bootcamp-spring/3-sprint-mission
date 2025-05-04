package com.sprint.mission.discodeit.dto.request;

public record UserCreateRequest (
        String userName,
        String email,
        String password,
        byte[] profileData,
        String contentType,
        String fileName
) {}
