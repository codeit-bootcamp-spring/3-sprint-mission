package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String name,
    boolean isActive,
    UUID profileImageId
) {

}