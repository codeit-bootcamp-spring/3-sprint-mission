package com.sprint.mission.discodeit.service.command;

import com.sprint.mission.discodeit.dto.data.BinaryContentData;

public record CreateUserCommand(
    String email,
    String username,
    String password,
    BinaryContentData profile
) {

}
