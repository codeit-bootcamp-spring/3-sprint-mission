package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record UserUpdateRequest (
        UUID userId,
        String userName,
        String email,
        String password,
        byte[] profileData,
        String contentType,
        String fileName
) {}
