package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserUpdateRequest(
    UUID id,
    String name,
    String password,
    UUID profileImageId
) {

}