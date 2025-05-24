package com.sprint.mission.discodeit.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserUpdateRequest(
    String newUsername,
    String newEmail,
    String newPassword
) {

}
