package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateRequest(
    UUID id,
    String name,
    String password,
    UUID profileImageId
) {

}