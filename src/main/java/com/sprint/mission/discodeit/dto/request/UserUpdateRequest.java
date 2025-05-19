package com.sprint.mission.discodeit.dto.request;

public record UserUpdateRequest(
    String NewUsername,
    String NewEmail,
    String NewPassword
) {

}