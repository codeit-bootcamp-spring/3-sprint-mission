package com.sprint.mission.discodeit.service.command;

import com.sprint.mission.discodeit.vo.BinaryContentData;

public record CreateUserCommand(
    String email,
    String username,
    String password,
    BinaryContentData profile
) {

}
