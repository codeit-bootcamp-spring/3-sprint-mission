package com.sprint.mission.discodeit.dto.User;

public record UserUpdateRequest(
        String newUsername,
        String newEmail,
        String newPassword
) {

}
